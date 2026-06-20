package org.dtt.mscatalog.application.dto.response.Inventory;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.response.ProductImgResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SaleProductResponse (
        UUID id,
        UUID productId,
        BigDecimal price,
        Integer stock,
        List<ProductImgResponse> images
) implements ISellableInventory {
}
