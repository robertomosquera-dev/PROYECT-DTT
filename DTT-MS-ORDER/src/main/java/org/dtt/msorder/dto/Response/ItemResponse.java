package org.dtt.msorder.dto.Response;

import lombok.Builder;
import org.dtt.msorder.model.ProductType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ItemResponse(
        UUID id,
        ProductType type,
        String title,

        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,

        String pictureUrl
) {
}
