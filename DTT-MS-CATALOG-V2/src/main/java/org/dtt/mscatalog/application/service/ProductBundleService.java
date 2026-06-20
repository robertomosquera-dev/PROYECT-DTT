package org.dtt.mscatalog.application.service;

import jakarta.transaction.Transactional;
import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.*;
import org.dtt.mscatalog.application.dto.request.ItemRequest;
import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;
import org.dtt.mscatalog.application.exception.CategoryNotFoundException;
import org.dtt.mscatalog.application.exception.ProductNotFoundException;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.*;
import org.dtt.mscatalog.application.port.in.productBundleUseCase.*;
import org.dtt.mscatalog.application.port.out.*;
import org.dtt.mscatalog.domain.model.*;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Enum.ProductType;
import org.dtt.mscatalog.infrastructure.mapper.ProductBundleMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for managing product bundles, including their creation, update, activation,
 * deactivation, stock management, and association with categories, images, and items.
 * This class extends {@link BaseInventoryService} and implements various use case interfaces
 * to provide a cohesive set of operations for product bundles.
 *
 * Responsibilities:
 * - Create product bundles with associated categories, items, and images.
 * - Update product bundle properties such as name, description, price, and stock.
 * - Find product bundles by their unique identifiers.
 * - Manage stock levels for product bundles during reservation or cancellation processes.
 * - Assign and remove categories or images associated with a product bundle.
 * - Maintain the relationship between product bundles and other entities such as items
 *   and images.
 *
 * Decorated with Spring's {@code @Service} to denote a service layer component
 * and {@code @Transactional} to ensure transactional integrity during operations.
 *
 * Caching:
 * - Utilizes {@code @CacheEvict} to clear related cache entries when there are updates
 *   to the product bundles, ensuring stale data is not served.
 *
 * Dependencies:
 * - {@link ProductBundleMapper}: Responsible for transforming between domain
 *   and response/request models.
 * - {@link CategoryRepositoryPort}: Handles category persistence and retrieval.
 * - {@link ImageService}: Manages image operations such as upload and retrieval.
 * - {@link SaleProductRepositoryPort}: Provides access to sale product data used
 *   for bundle items.
 */
@Service
@Transactional
public class ProductBundleService
        extends BaseInventoryService<ProductBundle, ProductBundleRepositoryPort>
        implements IProductBaseService<ProductBundleResponse, ProductBundleRequest, ProductBundleUpdateRequest, UUID>,
        CreateProductBundleUseCase, UpdateProductBundleUseCase, ActivateInventoryItemUseCase, DeactivateInventoryItemUseCase,
        ReplenishStockUseCase, WithdrawStockUseCase, FindProductBundleUseCase, AssignCategoryToBundleUseCase, AssignImgToBundleUserCase,
        CreateItemForBundle, RemoveCategoryFromBundle,RemoveImageFromBundle, RemoveItemFromBundle{

    private final ProductBundleMapper mapper;
    private final CategoryRepositoryPort categoryRepository;
    private final ImageService imageService;
    private final SaleProductRepositoryPort saleProductRepositoryPort;
    private final BundleItemRepositoryPort bundleItemRepositoryPort;

    public ProductBundleService(
            ProductBundleRepositoryPort repository,
            ProductRepositoryPort productRepository,
            ProductBundleMapper mapper,
            CategoryRepositoryPort categoryRepository,
            ImageService ImageService,
            SaleProductRepositoryPort saleProductRepositoryPort,
            BundleItemRepositoryPort bundleItemRepositoryPort
    ) {

        super(repository, productRepository);
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
        this.imageService = ImageService;
        this.saleProductRepositoryPort = saleProductRepositoryPort;
        this.bundleItemRepositoryPort = bundleItemRepositoryPort;
    }

    /**
     * Creates a new product bundle along with its associated images, categories, and items.
     * The method validates the input data, enforces mandatory fields, and manages the
     * persistence of the product bundle and its components.
     *
     * @param request the request object containing details about the product bundle to be created,
     *                including its categories and bundle items.
     * @param images a list of image requests representing the product images to be associated
     *               with the created product bundle.
     * @return a {@code ProductBundleResponse} object representing the created product bundle,
     *         including its details and associated images.
     * @throws IllegalArgumentException if required fields such as categories or bundle items
     *                                  are missing, or if any provided categories or products
     *                                  are not found.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse create(ProductBundleRequest request, List<ProductImgRequest> images) {

        Set<UUID> categoryIds = request.categories();
        List<BundleItemRequest> itemRequests = request.items();

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one category is required"
            );
        }

        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one bundle item is required"
            );
        }

        Set<UUID> saleProductIds = itemRequests.stream()
                .map(BundleItemRequest::productSaleId)
                .collect(Collectors.toSet());

        Set<Category> categories =
                categoryRepository.findByIds(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new IllegalArgumentException(
                    "Some categories were not found"
            );
        }

        List<SaleProduct> saleProducts =
                saleProductRepositoryPort.findAllByProductIdIn(saleProductIds);

        if (saleProducts.size() != saleProductIds.size()) {
            throw new IllegalArgumentException(
                    "Some sale products were not found"
            );
        }

        Map<UUID, SaleProduct> saleProductMap =
                saleProducts.stream()
                        .collect(Collectors.toMap(
                                SaleProduct::getId,
                                Function.identity()
                        ));

        ProductBundle bundle = mapper.toDomain(request);

        bundle.refreshCategories(categories);

        bundle.activate();


        for (BundleItemRequest itemRequest : itemRequests) {
            SaleProduct saleProduct = saleProductMap.get(itemRequest.productSaleId());
            BundleItem bundleItem = BundleItem.builder().build();
            bundleItem.updateSaleProduct(saleProduct);
            bundleItem.updateWeight(itemRequest.weight());
            bundle.addItem(bundleItem);
        }

        bundle = repository.save(bundle);

        List<ImageResponse> imageResponses =
                imageService.uploadProductImages(
                        bundle.getId(),
                        images,
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(
                bundle,
                imageResponses
        );
    }

    @Override //No borrar, evita problemas con la interfaces generica
    public ProductBundleResponse create(ProductBundleRequest request) {
        return null;
    }

    /**
     * Updates an existing product bundle using the provided update request.
     * The method retrieves the product bundle by its unique identifier,
     * applies the updates from the request, and saves the updated entity.
     * It also evicts related caches for consistency.
     *
     * @param id the unique identifier of the product bundle to update
     * @param request the object containing the updated information for the product bundle
     * @return a {@link ProductBundleResponse} containing the updated product bundle details
     * @throws ProductNotFoundException if no product bundle is found with the given identifier
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse update(UUID id, ProductBundleUpdateRequest request) {
        ProductBundle bundle = repository
                .findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        if (request.name() != null) {
            bundle.updateName(request.name());
        }

        if(request.description() != null) {
            bundle.updateDescription(request.description());
        }

        if (request.price() != null) {
            bundle.updatePrice(request.price());
        }

        if (request.stock() != null) {
            bundle.updateStock(request.stock());
        }

        ProductBundle updated = repository.save(bundle);

        List<ImageResponse> images = imageService.listImagesByOwnerId(updated.getId(), OwnerType.BUNDLE);

        return mapper.toResponse(updated, images);
    }

    /**
     * Retrieves a ProductBundleResponse object based on the provided unique identifier.
     *
     * @param id the unique identifier of the product bundle to find
     * @return a ProductBundleResponse containing the details of the product bundle and its associated images
     * @throws ProductNotFoundException if no product bundle with the specified id is found
     */
    @Override
    public ProductBundleResponse findById(UUID id) {
        ProductBundle bundle = repository
                .findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        List<ImageResponse> images = imageService.listImagesByOwnerId(bundle.getId(), OwnerType.BUNDLE);

        return mapper.toResponse(bundle,images);
    }

    /**
     * Processes stock for a list of item requests by reserving or withdrawing
     * stock for products of type BUNDLE and updating the repository.
     *
     * @param requests the list of {@link ItemRequest} objects containing
     *                 information about the products and quantities to be processed.
     * @return a list of {@link ReservationItemStock} objects reflecting the reserved
     *         stock for the requested items.
     * @throws IllegalArgumentException if any of the requested products are not
     *                                  found in the repository.
     * @throws ProductNotFoundException if a specific product in the request cannot
     *                                  be found in the repository.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public List<ReservationItemStock> processStock(
            List<ItemRequest> requests) {

        Map<UUID, ItemRequest> bundleItems = requests
                .stream()
                .filter(item -> item.type().equals(ProductType.BUNDLE))
                .collect(Collectors.toMap(
                        ItemRequest::productId,
                        Function.identity()
                ));

        List<UUID> productIds = new ArrayList<>(bundleItems.keySet());

        List<ProductBundle> bundleProducts =
                repository.findAllByIdsIn(productIds);

        if (bundleProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("No se han encontrado algunos productos");
        }

        Map<UUID, ProductBundle> productMap =
                bundleProducts.stream()
                        .collect(Collectors.toMap(
                                ProductBundle::getId,
                                Function.identity()
                        ));

        List<ReservationItemStock> reservationItems =
                new ArrayList<>();

        for (Map.Entry<UUID, ItemRequest> entry : bundleItems.entrySet()) {

            ProductBundle bundle =
                    productMap.get(entry.getKey());

            if (bundle == null) {
                throw new ProductNotFoundException(
                        entry.getKey()
                );
            }

            bundle.withdrawStock(
                    entry.getValue().quantity()
            );

            reservationItems.add(
                    ReservationItemStock.builder()
                            .productId(bundle.getId())
                            .type(ProductType.BUNDLE)
                            .quantity(entry.getValue().quantity())
                            .build()
            );
        }

        repository.saveAll(bundleProducts);

        return reservationItems;
    }

    /**
     * Cancels the stock for a list of reserved items and replenishes the stock
     * for bundle products accordingly. This method processes the reservation items,
     * identifies bundle products, and updates the stock by saving the updated product bundles.
     *
     * It clears the cache for "catalog-list" and "catalog" upon execution to ensure
     * that subsequent operations retrieve updated data.
     *
     * @param reservationItems a list of {@code ReservationItemStock} that represents
     *                         the items for which the stock will be canceled.
     *                         The items that belong to bundle products will have their
     *                         stock replenished.
     * @throws IllegalArgumentException if one or more bundle products are not found
     *                                  during the cancellation process.
     * @throws ProductNotFoundException if a specific product associated with a bundle
     *                                  is not found.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void cancelStock(List<ReservationItemStock> reservationItems) {

        Map<UUID, Integer> bundleItems = reservationItems.stream()
                .filter(item -> ProductType.BUNDLE.equals(item.getType()))
                .collect(Collectors.toMap(
                        ReservationItemStock::getProductId,
                        ReservationItemStock::getQuantity,
                        Integer::sum
                ));

        if (bundleItems.isEmpty()) {
            return;
        }

        List<UUID> productIds = new ArrayList<>(bundleItems.keySet());

        List<ProductBundle> bundleProducts = repository.findAllByIdsIn(productIds);

        if (bundleProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("No se han encontrado algunos productos del paquete durante la cancelacion");
        }

        Map<UUID, ProductBundle> productMap = bundleProducts.stream()
                .collect(Collectors.toMap(
                        ProductBundle::getId,
                        Function.identity()
                ));

        for (Map.Entry<UUID, Integer> entry : bundleItems.entrySet()) {

            ProductBundle bundle = productMap.get(entry.getKey());

            if (bundle == null) {
                throw new ProductNotFoundException(entry.getKey());
            }

            bundle.replenishStock(entry.getValue());
        }

        repository.saveAll(bundleProducts);
    }

    /**
     * Assigns a category to a product bundle using their unique identifiers.
     * This method clears relevant caches to ensure updated information is retrieved.
     *
     * @param id the unique identifier of the product bundle
     * @param categoryId the unique identifier of the category to be assigned
     * @return a ProductBundleResponse containing the result of the assignment operation
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse assignCategoryToBundle(UUID id, UUID categoryId) {
        ProductBundle bundle = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        bundle.addCategory(category);

        ProductBundle updated = repository.save(bundle);

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        updated.getId(),
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(updated, images);
    }

    /**
     * Assigns an image to a product bundle identified by the given ID.
     *
     * @param id the unique identifier of the product bundle
     * @param imageRequest the image request containing the image information to be assigned to the bundle
     * @return a ProductBundleResponse object containing the updated product bundle information
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse assignImgToBundle(UUID id, ProductImgRequest imageRequest) {
        ProductBundle bundle = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (imageRequest != null && imageRequest.image() != null) {

            ImageRequest request = ImageRequest.builder()
                    .ownerId(bundle.getId())
                    .ownerType(OwnerType.BUNDLE)
                    .image(imageRequest.image())
                    .build();

            imageService.uploadImage(request);
        }

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        bundle.getId(),
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(bundle, images);
    }

    /**
     * Creates a new item for the given bundle request and clears the relevant cache entries.
     *
     * @param bundleItemRequest the request object containing the details of the item to be created for the bundle
     * @return a response object containing details of the created bundle item
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse createItemForBundle(BundleItemRequest bundleItemRequest, UUID bundleId) {
        ProductBundle bundle = repository.findById(bundleId)
                .orElseThrow(() ->
                        new ProductNotFoundException(bundleId));

        SaleProduct saleProduct = saleProductRepositoryPort
                .findById(bundleItemRequest.productSaleId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                bundleItemRequest.productSaleId()));

        BundleItem item = BundleItem.builder().build();

        item.updateSaleProduct(saleProduct);
        item.updateWeight(bundleItemRequest.weight());

        item.updateBundle(bundle); //Quitar en caso de overflow

        bundle.addItem(item);

        ProductBundle updated = repository.save(bundle);

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        updated.getId(),
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(updated, images);
    }

    /**
     * Removes a specified category from a product bundle identified by its unique ID.
     * This operation will clear relevant cache entries to ensure data consistency.
     *
     * @param id the unique identifier of the product bundle from which the category is to be removed
     * @param categoryId the unique identifier of the category to be removed from the product bundle
     * @return a response object containing the details of the updated product bundle after the category removal
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse removeCategoryFromBundle(UUID id, UUID categoryId) {
        ProductBundle bundle = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        bundle.removeCategory(categoryId);

        ProductBundle updated = repository.save(bundle);

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        updated.getId(),
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(updated, images);
    }

    /**
     * Removes an image from the specified product bundle.
     *
     * @param id the unique identifier of the product bundle from which the image will be removed
     * @param imageId the unique identifier of the image to be removed from the product bundle
     * @return a {@code ProductBundleResponse} containing details of the updated product bundle after the image is removed
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse removeImageFromBundle(UUID id, UUID imageId) {
        ProductBundle bundle = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        imageService.deleteImageById(imageId);

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        id,
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(bundle, images);
    }

    /**
     * Removes an item from a product bundle.
     *
     * @param bundleId the unique identifier of the product bundle
     * @param itemId the unique identifier of the item to be removed from the bundle
     * @return the response containing the updated product bundle details
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public ProductBundleResponse removeItemFromBundle(UUID bundleId, UUID itemId) {
        ProductBundle bundle = repository.findById(bundleId)
                .orElseThrow(() ->
                        new ProductNotFoundException(bundleId));

        bundle.removeItem(itemId);
        ProductBundle updated = repository.save(bundle);

        List<ImageResponse> images =
                imageService.listImagesByOwnerId(
                        updated.getId(),
                        OwnerType.BUNDLE
                );

        return mapper.toResponse(updated, images);
    }

}
