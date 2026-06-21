package org.dtt.msorder.dto.Response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;

@Builder
public record OrderResponse(
    UUID orderId,
    String mpPreferenceId,
    String initPoint,
    String payerName,
    String payerEmail,
    Integer totalItems,
    BigDecimal totalAmount,
    OrderStatus orderStatus,
    PaymentStatus paymentStatus
) {}