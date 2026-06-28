package org.dtt.msorder.dto.Request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderPaymentConfirmationRequest(
        UUID orderId,
        String mpPaymentId,
        String status,
        String paymentStatus,
        LocalDateTime paidAt,
        String email,
        String customerName,
        BigDecimal total,
        String currency,
        List<PaymentConfirmationItemRequest> items
) {}
