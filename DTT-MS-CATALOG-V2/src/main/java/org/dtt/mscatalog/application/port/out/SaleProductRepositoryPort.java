package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.domain.model.SaleProduct;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface SaleProductRepositoryPort extends InventoryRepositoryPort<SaleProduct>{
    List<SaleProduct> findAllByProductIdIn(Set<UUID> productIds);
}
