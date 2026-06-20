package org.dtt.mscatalog.application.service;

import org.dtt.mscatalog.application.dto.ISellableInventory;

// Interface generica para los servicios de productos
// Se implmenta en cada clase solo create y update
// El padre lo implementa los de mas
public interface IProductBaseService<T, R extends ISellableInventory,U extends ISellableInventory, K> {
    T create(R request);
    T update(K id,U request);
    T findById(K id);
    void replenishStock(K id, Integer quantity);
    void withdrawStock(K id, Integer quantity);
    void activateProduct(K id);
    void deactivateProduct(K id);
}
