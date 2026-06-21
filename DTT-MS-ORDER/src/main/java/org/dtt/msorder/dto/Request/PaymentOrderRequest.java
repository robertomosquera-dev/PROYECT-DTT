package org.dtt.msorder.dto.Request;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record PaymentOrderRequest (
        UUID orderId,
        List<PaymentItemRequest> items,
        PayerRequest payer,
        String platform
) {
}
