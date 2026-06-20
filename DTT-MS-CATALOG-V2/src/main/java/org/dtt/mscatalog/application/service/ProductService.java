package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.application.dto.request.*;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.ProductDetailsResponse;
import org.dtt.mscatalog.application.dto.response.ProductResponse;
import org.dtt.mscatalog.application.exception.CategoryNotFoundException;
import org.dtt.mscatalog.application.exception.ProductNotFoundException;
import org.dtt.mscatalog.application.port.in.productUseCase.*;
import org.dtt.mscatalog.application.port.out.CategoryRepositoryPort;
import org.dtt.mscatalog.application.port.out.ProductRepositoryPort;
import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Product;
import org.dtt.mscatalog.infrastructure.mapper.ProductMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service class responsible for managing operations related to products.
 * This includes assigning categories or images to products, changing their status, creating, updating,
 * deleting, and querying products.
 *
 * Implements various use cases for handling product-related business logic, such as:
 * - Assigning a category to a product
 * - Assigning images to a product
 * - Changing the status of a product
 * - Creating a new product
 * - Deleting a product
 * - Retrieving product details by ID or name
 * - Listing products with filters and pagination
 * - Removing a category or image from a product
 *
 * Utilizes transaction management and caching to ensure consistent state and improved
 * performance in repetitive reads.
 *
 * Dependencies include:
 * - ProductRepositoryPort for persistence operations
 * - ProductMapper for domain-to-DTO conversions
 * - ImageService for managing image-related operations
 * - CategoryRepositoryPort for fetching category details
 */
@Service
@RequiredArgsConstructor
public class ProductService implements
        AssignCategoryToProductUseCase, AssignImgToProductUseCase,
        ChangeStatusProductUseCase, CreateProductUseCase,
        DeleteProductUseCase, FindProductUseCase,
        ListProductUseCase, UpdateProductUseCase,
        RemoveImageFromProduct, RemoveCategoryFromProduct {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMapper productMapper;
    private final ImageService imageService;
    private final CategoryRepositoryPort categoryRepositoryPort;

    /**
     * Assigns a category to a product by the given product ID and category ID.
     * If the product or category does not exist, a corresponding exception will be thrown.
     * The operation updates the product details and clears the cache for the product list,
     * while updating the cache for the specific product.
     *
     * @param id the unique identifier of the product to which the category is to be assigned
     * @param categoryId the unique identifier of the category to assign to the product
     * @return a {@link ProductResponse} object containing updated product details,
     *         including its associated images and categories
     * @*/
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products-list", allEntries = true)}
    )
    public ProductResponse assignCategoryToProductId(UUID id, UUID categoryId) {
        Product product = productRepositoryPort
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        Category category = categoryRepositoryPort
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        product.addCategory(category);
        product = productRepositoryPort.save(product);
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }


    /**
     * Assigns an image to a product and updates the product's image list.
     *
     * This method uploads a new image for the product specified by the given ID, associates
     * the image with the product, and retrieves an updated list of images for the product.
     * The updated product information, including its associated images, is then returned.
     *
     * @param id the unique identifier of the product to which the image will be assigned
     * @param productImgRequest the request object containing the image data to be assigned to the product
     * @return a {@code ProductResponse} object representing the updated product with its associated images
     * @throws ProductNotFoundException if no product is found with the given ID
     */
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products-list", allEntries = true)}
    )
    public ProductResponse assignImgToProduct(UUID id, ProductImgRequest productImgRequest) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (productImgRequest != null && productImgRequest.image() != null) {
            ImageRequest imageRequest = ImageRequest.builder()
                    .ownerId(product.getId())
                    .ownerType(OwnerType.PRODUCT)
                    .image(productImgRequest.image())
                    .build();
            imageService.uploadImage(imageRequest);
        }
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }

    /**
     * Changes the status of a product based on the provided status request.
     * This method updates the product's status to either ENABLED or DISABLED,
     * and evicts relevant caches for consistency.
     *
     * @param id the UUID of the product whose status is to be changed
     * @param status the desired status of the product, either ENABLED or DISABLED
     * @throws ProductNotFoundException if no product is found with the given ID
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products-list", allEntries = true)
    })
    public void changeStatusProduct(UUID id, StatusRequest status) {
        Product product = productRepositoryPort
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        switch (status) {
            case ENABLED -> product.enable();
            case DISABLED -> product.disable();
        }
        productRepositoryPort.save(product);
    }

    /**
     * Creates a new product based on the given product request data, associates the product
     * with the specified categories, and uploads related images.
     *
     * @param productRequest the request object containing the details for creating a product,
     *                       including name, description, category IDs, and images
     * @return the response object containing the details of the created product, including
     *         associated categories and uploaded images*/
    @Transactional
    @Override
    @CacheEvict(value = "products-list", allEntries = true)
    public ProductResponse createProduct(ProductRequest productRequest) {
        Set<Category> categories = categoryRepositoryPort.findByIds(productRequest.categoryIds());
        if (productRequest.categoryIds().size() != categories.size()) {
            throw new IllegalArgumentException("Some categories not found");
        }
        Product product = productMapper.toDomain(productRequest);
        product.refreshCategories(categories);
        product = productRepositoryPort.save(product);
        List<ImageResponse> imageResponses = imageService.uploadProductImages(product.getId(), productRequest.images(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, imageResponses);
    }

    /**
     * Deletes a product by its unique identifier. This method performs the following actions:
     * - Validates the existence of the product by its ID.
     * - Deletes any associated images related to the product.
     * - Marks the product as deleted and saves the updated state.
     * - Evicts related cache entries for the product and product lists.
     *
     * @param id the unique identifier of the product to be deleted
     * @throws ProductNotFoundException if the product with the given ID does not exist
     */
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products-list", allEntries = true)
    })
    public void deleteProduct(UUID id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        imageService.deleteImagesByOwner(product.getId(), OwnerType.PRODUCT);
        product.delete();
        productRepositoryPort.save(product);
    }

    /**
     * Finds a product by its unique identifier and returns the corresponding product response.
     * Retrieves associated images for the product and maps the data to a product response object.
     * The result of this method is cached for performance optimization.
     *
     * @param id the unique identifier of the product to be retrieved
     * @return the product response containing product details and associated images
     * @throws ProductNotFoundException if no product is found with the given id
     */
    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductResponse findById(UUID id) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }

    /**
     * Retrieves a product by its name and maps it to a response object.
     *
     * @param name the name of the product to be retrieved
     * @return the product response containing product details and associated images
     * @throws ProductNotFoundException if no product is found with the specified name
     */
    @Override
    @Cacheable(value = "products", key = "#name")
    public ProductResponse findByName(String name) {
        Product product = productRepositoryPort.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException(name));
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }

    /**
     * Updates an existing product by its unique identifier and updates its associated information
     * based on the provided product update request. Caches the updated product and clears the
     * product list cache.
     *
     * @param id the unique identifier of the product to be updated
     * @param productRequest the request object containing updated product information such as
     *                       name and description
     * @return a response object containing the updated product information along with associated images
     * @throws ProductNotFoundException if no product is found with the provided identifier
     */
    @Override
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products-list", allEntries = true)}
    )
    public ProductResponse update(UUID id, ProductUpdateRequest productRequest) {
        Product product = productRepositoryPort
                .findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.updateName(productRequest.name());
        product.updateDescription(productRequest.description());
        product = productRepositoryPort.save(product);
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }


    /**
     * Retrieves a list of products based on the given filter and pagination details.
     * The method is read-only, utilizes caching for performance, and maps product entities
     * to their detailed response form, including the primary image for each product.
     *
     * @param filter the criteria used to filter the products, such as category, price range, or availability.
     * @param pageRequest the pagination details, including page number and size.
     * @return a list of product details response objects that match the given filter and pagination criteria.
     *         If no products match, an empty list is returned.
     */
    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "products-list", key = "#filter.toString() + '_' + #pageRequest.toString()")
    public List<ProductDetailsResponse> listProducts(ProductFilter filter, PageRequest pageRequest) {
        List<Product> products = productRepositoryPort.listProducts(filter, pageRequest);
        if (products.isEmpty()) {
            return List.of();
        }
        List<UUID> productIds = products.stream().map(Product::getId).toList();
        Map<UUID, ImageResponse> primaryImagesMap = imageService.getPrimaryImagesMap(productIds, OwnerType.PRODUCT);
        return products.stream()
                .map(product -> {
                    ImageResponse primaryImage = primaryImagesMap.get(product.getId());
                    return productMapper.toDetailsResponse(product, primaryImage);
                })
                .toList();
    }


    /**
     * Removes the specified category from the product identified by the provided ID.
     *
     * @param id the unique identifier of the product from which the category will be removed
     * @param categoryId the unique identifier of the category to be removed from the product
     * @return a ProductResponse containing the updated product details after the category has been removed
     * @throws ProductNotFoundException if a product with the given ID is not found
     */
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products-list", allEntries = true)}
    )
    public ProductResponse removeCategoryFromProduct(UUID id, UUID categoryId) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.removeCategory(categoryId);
        product = productRepositoryPort.save(product);
        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);
        return productMapper.toResponse(product, images);
    }


    /**
     * Removes an image associated with a product and updates the product's image list.
     * The image is deleted from storage, and the updated image list is retrieved.
     *
     * @param id      the unique identifier of the product
     * @param imageId the unique identifier of the image to be removed
     * @return a {@code ProductResponse} containing the updated product details along with the remaining images
     */
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "products", key = "#id")},
            evict = {@CacheEvict(value = "products-list", allEntries = true)}
    )
    public ProductResponse removeImageFromProduct(UUID id, UUID imageId) {
        Product product = productRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        imageService.deleteImageById(imageId);
        List<ImageResponse> updatedImages = imageService.listImagesByOwnerId(id, OwnerType.PRODUCT);
        return productMapper.toResponse(product, updatedImages);
    }
}