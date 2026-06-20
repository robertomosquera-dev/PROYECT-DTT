package org.dtt.mscatalog.application.port.in.productBundleUseCase;

import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;

import java.util.UUID;

public interface AssignCategoryToBundleUseCase {
    ProductBundleResponse assignCategoryToBundle(UUID id, UUID categoryId);
}
