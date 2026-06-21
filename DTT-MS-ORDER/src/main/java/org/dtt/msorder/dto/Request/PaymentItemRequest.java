package org.dtt.msorder.dto.Request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentItemRequest(
        UUID productId,
        String title,
        String description,
        String category,
        Integer quantity,
        String currency,
        BigDecimal unitPrice,
        String pictureUrl
) {
}
