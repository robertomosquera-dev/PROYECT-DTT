package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.application.dto.request.CategoryRequest;
import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.request.StatusRequest;
import org.dtt.mscatalog.application.dto.request.UpdateCategoryBasicInfoRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.dtt.mscatalog.application.dto.response.CategoryTreeResponse;
import org.dtt.mscatalog.application.exception.CategoryNotFoundException;
import org.dtt.mscatalog.application.exception.SlugAlreadyExistsException;
import org.dtt.mscatalog.application.port.in.categoryUseCase.*;
import org.dtt.mscatalog.application.port.out.CategoryRepositoryPort;
import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.infrastructure.mapper.CategoryMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides services for managing category entities in the system. This service
 * supports creating, updating, deleting, and retrieving category information,
 * as well as managing hierarchical relationships and associated images.
 *
 * Implements multiple use cases such as creating a new category, updating
 * category details, finding categories by various attributes, listing categories,
 * and managing their status and associated images.
 *
 * Caching is applied to various operations to improve performance and reduce
 * database interactions. Cache invalidation is handled using annotations where
 * appropriate, ensuring data consistency for dependent operations.
 *
 * Features:
 * - Create, update, delete, and find categories.
 * - Manage hierarchical relationships via parent-child structures.
 * - Support for category image upload, update, and deletion.
 * - Retrieve categories by different attributes such as ID, slug, name, and path.
 * - Handle paginated and filtered listing of categories.
 * - Generate hierarchical views of categories as trees.
 * - Status management including enabling and disabling categories.
 *
 * Dependencies:
 * - {@link CategoryRepositoryPort} for data persistence and retrieval.
 * - {@link CategoryMapper} for converting domain objects to responses and vice versa.
 * - {@link ImageService} for image upload and management.
 *
 * Exceptions:
 * - Throws {@link CategoryNotFoundException} if a requested category cannot be found.
 * - Throws {@link SlugAlreadyExistsException} if a duplicate slug is detected.
 *
 * Annotations:
 * - {@code @Service} indicates this class is a Spring-managed service.
 * - {@code @RequiredArgsConstructor} generates a constructor for final fields.
 * - {@code @Slf4j} enables logging for debugging and monitoring purposes.
 *
 * Caching Strategies:
 * - {@code @Cacheable} for returning cached data when available.
 * - {@code @CacheEvict} for clearing cache when data is updated or deleted.
 * - {@code @CachePut} for updating cache with fresh data.
 *
 * Transaction Management:
 * - {@code @Transactional} is used to ensure atomicity for operations involving
 *   multiple database actions, such as creating a category with an uploaded image,
 *   or updating hierarchical structures.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements
        ChangeStatusCategoryUseCase, CreateCategoryUseCase,
        DeleteCategoryUseCase, FindCategoryUseCase,
        ListCategoriesUseCase, TreeCategoriesUseCase,
        UpdateCategoryUseCase, UpdateImageCategoryUseCase,
        DeleteImgCategoryUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    /**
     * Changes the status of a category identified by its unique ID.
     * The method updates the status of the category to either ENABLED or DISABLED
     * based on the provided {@code status} parameter. It also evicts the relevant
     * cache entries to reflect the status change in cached data.
     *
     * @param id the unique identifier of the category to be updated
     * @param status the new status to be applied to the category
     *               (can be either ENABLED or DISABLED)
     * @throws CategoryNotFoundException if a category with the given ID does not exist
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories",      key = "#id"),
            @CacheEvict(value = "categories-list", allEntries = true),
            @CacheEvict(value = "categories-tree", allEntries = true)
    })
    public void changeStatusCategory(UUID id, StatusRequest status) {
        Category category = categoryRepositoryPort
                .findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        switch (status) {
            case ENABLED  -> category.enable();
            case DISABLED -> category.disable();
        }
        categoryRepositoryPort.save(category);
    }

    /**
     * Creates a new category based on the provided request and optional image.
     *
     * This method validates the slug for uniqueness and ensures that the parent category,
     * if specified, exists in the system. The category information is then saved to the repository,
     * and optionally, an image can be uploaded and associated with the category.
     *
     * @param categoryRequest the request object containing details of the category to be created,
     *                        including name, slug, and optional parent ID
     * @param image an optional image file to be associated with the category; can be null or empty
     * @return a {@link CategoryResponse} object containing the details of the newly created category,
     *         including its ID, name, slug, path, and optionally the URL of the uploaded image
     * @throws SlugAlreadyExistsException if a category with the provided slug already exists
     * @throws CategoryNotFoundException if the specified parent category does not exist
     */
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories-list", allEntries = true),
            @CacheEvict(value = "categories-tree", allEntries = true)
    })
    public CategoryResponse createCategory(CategoryRequest categoryRequest, MultipartFile image) {

        if (categoryRepositoryPort.existsBySlug(categoryRequest.slug())) {
            throw new SlugAlreadyExistsException(categoryRequest.slug());
        }

        Category parent = null;
        if (categoryRequest.parentId() != null) {
            parent = categoryRepositoryPort.findById(categoryRequest.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryRequest.parentId()));
        }

        Category category = categoryMapper.toDomain(categoryRequest);
        category.setParent(parent);
        category.refreshPath(parent);
        category = categoryRepositoryPort.save(category);

        String uploadedUrl = null;
        if (image != null && !image.isEmpty()) {
            ImageRequest imageRequest = ImageRequest.builder()
                    .ownerId(category.getId())
                    .ownerType(OwnerType.CATEGORY)
                    .image(image)
                    .build();
            uploadedUrl = imageService.uploadImage(imageRequest).url();
        }

        CategoryResponse response = categoryMapper.toResponse(category);

        if (uploadedUrl != null) {
            return response.toBuilder()
                    .imgUrl(uploadedUrl)
                    .build();
        }

        return response;
    }

    /**
     * Deletes a category and performs related cleanup operations such as removing associated images
     * and propagating the delete operation to child categories.
     *
     * The method evicts the related cache entries for categories, categories list,
     * and categories tree. If the category is not found, an exception is thrown.
     *
     * @param id the unique identifier of the category to be deleted
     * @throws CategoryNotFoundException if no category is found with the specified id
     */
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories",      key = "#id"),
            @CacheEvict(value = "categories-list", allEntries = true),
            @CacheEvict(value = "categories-tree", allEntries = true)
    })
    public void deleteCategory(UUID id) {
        Category category = categoryRepositoryPort
                .findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.delete();
        categoryRepositoryPort.save(category);
        imageService.deleteImagesByOwner(id, OwnerType.CATEGORY);
        propagateDeleteToChildren(category);
    }

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the unique identifier of the category to retrieve
     * @return the category response mapped from the found category entity
     * @throws CategoryNotFoundException if no category is found with the provided identifier
     */
    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse findById(UUID id) {
        return categoryRepositoryPort
                .findById(id)
                .map(categoryMapper::toResponse)
                .map(categoryResponse -> {
                    var imgs = imageService.listImagesByOwnerId(categoryResponse.id(), OwnerType.CATEGORY);
                    String imgUrl = imgs.isEmpty() ? null : imgs.getFirst().url();
                    return categoryResponse.toBuilder().imgUrl(imgUrl).build();
                })
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    /**
     * Retrieves a category by its slug.
     * The result is cached with a key based on the slug.
     *
     * @param slug the unique identifier or slug of the category to be retrieved
     * @return a {@code CategoryResponse} object representing the category details
     * @throws CategoryNotFoundException if no category is found with the specified slug
     */
    @Override
    @Cacheable(value = "categories", key = "#slug")
    public CategoryResponse findBySlug(String slug) {
        return categoryRepositoryPort
                .findBySlug(slug)
                .map(categoryMapper::toResponse)
                .map(categoryResponse -> {
                    var imgs = imageService.listImagesByOwnerId(categoryResponse.id(), OwnerType.CATEGORY);
                    String imgUrl = imgs.isEmpty() ? null : imgs.getFirst().url();
                    return categoryResponse.toBuilder().imgUrl(imgUrl).build();
                })
                .orElseThrow(() -> new CategoryNotFoundException("slug", slug));
    }

    /**
     * Finds a category by its name and returns the corresponding response object.
     *
     * @param name the name of the category to be searched
     * @return the response object containing category details
     * @throws CategoryNotFoundException if no category with the specified name is found
     */
    @Override
    @Cacheable(value = "categories", key = "#name")
    public CategoryResponse findByName(String name) {
        return categoryRepositoryPort
                .findByName(name)
                .map(categoryMapper::toResponse)
                .map(categoryResponse -> {
                    var imgs = imageService.listImagesByOwnerId(categoryResponse.id(), OwnerType.CATEGORY);
                    String imgUrl = imgs.isEmpty() ? null : imgs.getFirst().url();
                    return categoryResponse.toBuilder().imgUrl(imgUrl).build();
                })
                .orElseThrow(() -> new CategoryNotFoundException("name", name));
    }

    /**
     * Retrieves a category by its path and returns a response representation of the category.
     * The method is cached with the key derived from the provided path.
     *
     * @param path the unique path of the category to be retrieved
     * @return the response containing the category details
     * @throws CategoryNotFoundException if no category is found with the specified path
     */
    @Override
    @Cacheable(value = "categories", key = "#path")
    public CategoryResponse findByPath(String path) {
        return categoryRepositoryPort
                .findByPath(path)
                .map(categoryMapper::toResponse)
                .map(categoryResponse -> {
                    var imgs = imageService.listImagesByOwnerId(categoryResponse.id(), OwnerType.CATEGORY);
                    String imgUrl = imgs.isEmpty() ? null : imgs.getFirst().url();
                    return categoryResponse.toBuilder().imgUrl(imgUrl).build();
                })
                .orElseThrow(() -> new CategoryNotFoundException("path", path));
    }

    /**
     * Retrieves the category tree structure starting from a specified parent category ID.
     *
     * @param parentId the unique identifier of the parent category to build the tree structure from
     * @return a {@code CategoryTreeResponse} object containing the hierarchical tree structure of categories
     * @throws CategoryNotFoundException if no category is found for the provided {@code parentId}
     */
    @Override
    @Cacheable(value = "categories-tree", key = "#parentId")
    public CategoryTreeResponse getTreeCategoriesByParentId(UUID parentId) {
        Category category = categoryRepositoryPort
                .findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(parentId));
        return buildTree(category);
    }

    /**
     * Retrieves a list of categories based on the specified filter and pagination details.
     *
     * @param filter the criteria used to filter the categories
     * @param pageRequest the pagination and sorting information
     * @return a list of CategoryResponse objects matching the filter and pagination criteria
     */
    @Override
    @Cacheable(value = "categories-list", key = "#filter.toString() + '_' + #pageRequest.toString()")
    public List<CategoryResponse> listCategories(CategoryFilter filter, PageRequest pageRequest) {
        List<Category> categories = categoryRepositoryPort.listCategories(filter, pageRequest);

        List<UUID> categoryIds = categories.stream()
                .map(Category::getId)
                .toList();

        Map<UUID, String> imageUrlsByOwnerId =
                imageService.findPrimaryImageUrlsByOwnerIds(categoryIds, OwnerType.CATEGORY);

        return categories.stream()
                .map(categoryMapper::toResponse)
                .map(categoryResponse -> categoryResponse.toBuilder()
                        .imgUrl(imageUrlsByOwnerId.get(categoryResponse.id()))
                        .build())
                .toList();
    }

    /**
     * Updates an existing category with the given basic information.
     * The method updates the category's name, status, and slug if they are provided,
     * and propagates changes to child categories if the slug is updated. Caching
     * annotations are used to update and invalidate relevant cache entries.
     *
     * @param id the unique identifier of the category to be updated
     * @param updateRequest the object containing the updated basic information of the category,
     *                      such as name, status, and slug
     * @return a {@code CategoryResponse} object containing the updated details of the category
     * @throws CategoryNotFoundException if no category is found with the provided {@code id}
     */
    @Transactional
    @Override
    @Caching(
            put  = { @CachePut(value = "categories", key = "#id") },
            evict = {
                    @CacheEvict(value = "categories-list", allEntries = true),
                    @CacheEvict(value = "categories-tree", allEntries = true)
            }
    )
    public CategoryResponse updateCategory(UUID id, UpdateCategoryBasicInfoRequest updateRequest) {
        Category category = categoryRepositoryPort
                .findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (updateRequest.name() != null) {
            category.setName(updateRequest.name());
        }
        if (updateRequest.categoryStatus() != null) {
            category.setCategoryStatus(updateRequest.categoryStatus());
        }

        boolean slugChanged = false;
        if (updateRequest.slug() != null &&
                !category.getSlug().equals(updateRequest.slug())) {
            category.changeSlug(updateRequest.slug());
            slugChanged = true;
        }

        category = categoryRepositoryPort.save(category);

        if (slugChanged) {
            propagatePathChangeToChildren(category);
        }

        return categoryMapper.toResponse(category);
    }

    /**
     * Updates the image of a specific category identified by its UUID.
     *
     * If the provided image is non-null and not empty, the existing images associated with
     * the category are deleted, and the new image is uploaded. The method then updates the
     * category's image URL with the new image's URL and returns the updated category response.
     *
     * @param id    The unique identifier of the category whose image is to be updated.
     * @param image The new image to be associated with the category; must be a non-null, non-empty file.
     * @return A {@code CategoryResponse} object representing the updated category information,
     *         including the new image URL if provided.
     * @throws CategoryNotFoundException if no category with the specified UUID is found.
     */
    @Transactional
    @Override
    @Caching(
            put  = { @CachePut(value = "categories", key = "#id") },
            evict = {
                    @CacheEvict(value = "categories-list", allEntries = true),
                    @CacheEvict(value = "categories-tree", allEntries = true)
            }
    )
    public CategoryResponse updateCategoryImage(UUID id, MultipartFile image) {
        Category category = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (image != null && !image.isEmpty()) {
            imageService.deleteImagesByOwner(category.getId(), OwnerType.CATEGORY);

            ImageRequest imageRequest = ImageRequest.builder()
                    .ownerId(category.getId())
                    .ownerType(OwnerType.CATEGORY)
                    .image(image)
                    .build();

            String newUrl = imageService.uploadImage(imageRequest).url();

            return categoryMapper.toResponse(category)
                    .toBuilder()
                    .imgUrl(newUrl)
                    .build();
        }

        return categoryMapper.toResponse(category);
    }

    /**
     * Deletes an image associated with a specific owner identified by the provided ID
     * and evicts relevant cache entries to ensure consistency.
     *
     * @param id the unique identifier of the owner (UUID) whose associated image is to be deleted
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories",      key = "#id"),
            @CacheEvict(value = "categories-list", allEntries = true),
            @CacheEvict(value = "categories-tree", allEntries = true)
    })
    public void deleteImage(UUID id) {
        imageService.deleteImagesByOwner(id, OwnerType.CATEGORY);
    }


    /**
     * Builds a tree structure representation of the given category and its children.
     *
     * @param category the root category for which the tree is to be built
     * @return a CategoryTreeResponse object representing the tree structure of the category and its children
     */
    private CategoryTreeResponse buildTree(Category category) {
        List<Category> children = categoryRepositoryPort.findByParentId(category.getId());
        List<CategoryTreeResponse> childrenResponse = children.stream()
                .map(this::buildTree)
                .toList();
        return new CategoryTreeResponse(category.getId(), category.getName(), childrenResponse);
    }

    /**
     * Updates the path of a parent category and recursively propagates the changes
     * to all its child categories by recalculating their paths and saving the updated
     * categories to the repository.
     *
     * @param parent the parent category whose path change needs to be propagated
     *               to its child categories
     */
    private void propagatePathChangeToChildren(Category parent) {
        List<Category> children = categoryRepositoryPort.findByParentId(parent.getId());
        if (!children.isEmpty()) {
            children.forEach(child -> {
                child.recalculatePath();
                categoryRepositoryPort.save(child);
                propagatePathChangeToChildren(child);
            });
        }
    }

    /**
     * Propagates the deletion of a parent category to all its child categories recursively.
     * For each child category, deletes associated images and marks it as deleted.
     *
     * @param parent the parent category whose child categories need to be deleted
     */
    private void propagateDeleteToChildren(Category parent) {
        List<Category> children = categoryRepositoryPort.findByParentId(parent.getId());
        if (!children.isEmpty()) {
            for (Category child : children) {
                if (child.isDeleted()) continue;
                child.delete();
                categoryRepositoryPort.save(child);
                imageService.deleteImagesByOwner(child.getId(), OwnerType.CATEGORY);
                propagateDeleteToChildren(child);
            }
        }
    }
}