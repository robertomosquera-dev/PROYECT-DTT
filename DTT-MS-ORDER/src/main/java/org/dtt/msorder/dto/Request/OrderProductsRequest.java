package org.dtt.msorder.dto.Request;

import org.dtt.msorder.model.ProductType;

import java.util.List;
import java.util.UUID;

public record OrderProductsRequest(
        UUID orderId,
        List<ProductItem> itemList
) {
    public record ProductItem(
            UUID productId,
            ProductType type
    ) {
    }
}
