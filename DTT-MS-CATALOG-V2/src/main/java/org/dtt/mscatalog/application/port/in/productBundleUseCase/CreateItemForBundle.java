package org.dtt.mscatalog.application.port.in.productBundleUseCase;

import org.dtt.mscatalog.application.dto.request.Inventory.BundleItemRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;

import java.util.UUID;

public interface CreateItemForBundle {
    ProductBundleResponse createItemForBundle(BundleItemRequest bundleItemRequest, UUID bundleId);
}
