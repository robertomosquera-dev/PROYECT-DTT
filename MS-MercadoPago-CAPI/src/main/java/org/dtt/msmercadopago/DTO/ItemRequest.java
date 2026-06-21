package org.dtt.msmercadopago.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemRequest(
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
