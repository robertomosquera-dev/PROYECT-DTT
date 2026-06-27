package org.dtt.msorder.dto.Response;

import lombok.Builder;
import org.dtt.msorder.model.ProductType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ItemResponse(
        UUID itemId,
        UUID productId,
        ProductType type,
        String name,

        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,

        String imageUrl
) {
}
