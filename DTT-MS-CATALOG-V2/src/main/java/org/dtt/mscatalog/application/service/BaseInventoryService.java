package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.InventoryRepositoryPort;
import org.dtt.mscatalog.application.port.out.ProductRepositoryPort;
import org.dtt.mscatalog.domain.model.BaseInventoryItem;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Abstract base service that provides common inventory management operations.
 * This service is designed to work with inventory item entities and their associated repositories.
 *
 * @param <T> the type of the inventory item, which must extend {@link BaseInventoryItem}
 * @param <R> the type of the repository port, which must implement {@link InventoryRepositoryPort}
 */
@RequiredArgsConstructor
public abstract class BaseInventoryService
        <T extends BaseInventoryItem, R extends InventoryRepositoryPort<T>> {

    protected final R repository;
    protected final ProductRepositoryPort productRepository;

    /**
     * Replenishes the stock of an inventory item with the specified quantity.
     *
     * @param id the unique identifier of the inventory item whose stock needs to be replenished.
     * @param quantity the quantity to be added to the inventory item's stock. Must be a non-negative value.
     * @throws NoSuchElementException if no inventory item is found with the given ID.
     */
    @Transactional
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void replenishStock(UUID id, Integer quantity) {
        T entity = repository.findById(id).orElseThrow();
        entity.replenishStock(quantity);
        repository.save(entity);
    }

    /**
     * Deducts a specified quantity of stock from the inventory item identified by its unique ID.
     * If the requested quantity exceeds the available stock, an {@code InsufficientStockException} is thrown.
     * After successfully withdrawing the stock, the changes are saved to the repository.
     * The cache entries for "catalog-list" and "catalog" are evicted to ensure data consistency.
     *
     * @param id the unique identifier of the inventory item whose stock is to be withdrawn
     * @param quantity the amount of stock to be withdrawn from the inventory item
     * @throws NoSuchElementException if no inventory item with the given ID is found
     * @throws InsufficientStockException if the available stock is less than the requested quantity
     */
    @Transactional
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void withdrawStock(UUID id, Integer quantity) {
        T entity = repository.findById(id).orElseThrow();
        entity.withdrawStock(quantity);
        repository.save(entity);
    }

    /**
     * Activates a product identified by the given UUID. The product's status is set
     * to active, and the updated state is persisted to the repository. If the product
     * is already active, an exception is thrown.
     *
     * @param id the unique identifier of the product to activate
     * @throws ProductAlreadyActiveException if the product is already active
     * @throws NoSuchElementException if no product with the given id is found
     */
    @Transactional
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void activateProduct(UUID id) {
        T entity = repository.findById(id).orElseThrow();
        entity.activate();
        repository.save(entity);
    }

    /**
     * Deactivates the product with the specified identifier. The product's status
     * is set to inactive, and the changes are persisted to the repository. Any cached
     * entries related to the product catalog are invalidated.
     *
     * @param id the unique identifier of the product to be deactivated
     * @throws ProductAlreadyInactiveException if the product is already inactive
     * @throws NoSuchElementException if no product with the given identifier is found
     */
    @Transactional
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void deactivateProduct(UUID id) {
        T entity = repository.findById(id).orElseThrow();
        entity.deactivate();
        repository.save(entity);
    }
}