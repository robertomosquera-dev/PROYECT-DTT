package org.dtt.msorder.dto.Response;

import lombok.Builder;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderDTO(
        UUID orderId,
        String mpPreferenceId,
        String initPoint,
        String payerName,
        String payerEmail,
        Integer totalItems,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        List<ItemResponse> items
) {
}
