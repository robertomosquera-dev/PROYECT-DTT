package org.dtt.mscatalog.application.port.in.productCatalogUseCase;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.util.UUID;

public interface FindProductCatalogUseCase {
    ISellableInventory findById(UUID id, ProductType type);
}
