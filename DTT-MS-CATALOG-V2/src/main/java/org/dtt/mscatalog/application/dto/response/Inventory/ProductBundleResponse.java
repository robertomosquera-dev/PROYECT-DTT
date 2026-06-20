package org.dtt.mscatalog.application.dto.response.Inventory;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.request.Inventory.BundleItemRequest;
import org.dtt.mscatalog.application.dto.response.BundleItemResponse;
import org.dtt.mscatalog.application.dto.response.ProductImgResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductBundleResponse(
        UUID id,
        BigDecimal price,
        Integer stock,
        String name,
        List<BundleItemResponse> items,
        List<ProductImgResponse> images
) implements ISellableInventory {
}
