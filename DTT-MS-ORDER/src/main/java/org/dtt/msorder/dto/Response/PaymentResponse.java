package org.dtt.msorder.dto.Response;

import org.dtt.msorder.model.PaymentStatus;

public record PaymentResponse(
        String mpPreferenceId,
        String initPoint,
        String orderId,
        PaymentStatus status
) {}