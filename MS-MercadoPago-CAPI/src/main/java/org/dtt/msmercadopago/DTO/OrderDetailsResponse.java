package org.dtt.msmercadopago.DTO;


import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderDetailsResponse(
        UUID id,
        String mpPreferenceId,
        BigDecimal total,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        int totalItems
) {
}
