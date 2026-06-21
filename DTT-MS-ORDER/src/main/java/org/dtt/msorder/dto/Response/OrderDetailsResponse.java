package org.dtt.msorder.dto.Response;

import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderDetailsResponse(
        UUID id,
        String orderCode,
        String mpPreferenceId,
        BigDecimal total,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        Integer totalItems,
        Integer totalPerItem
) {
}
