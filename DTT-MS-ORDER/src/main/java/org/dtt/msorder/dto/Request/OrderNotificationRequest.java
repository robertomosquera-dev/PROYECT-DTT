package org.dtt.msorder.dto.Request;

import lombok.Builder;
import org.dtt.msorder.model.PaymentStatus;

import java.util.List;

@Builder
public record OrderNotificationRequest(
        String mpPreferenceId,
        String initPoint,
        String orderId,
        PaymentStatus status,
        String email,
        List<PaymentItemRequest> items
) {
}
