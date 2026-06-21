package org.dtt.msorder.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemReservationResponse(
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
