package org.dtt.msmercadopago.DTO;

public record PaymentResponse(
        String mpPreferenceId,
        String initPoint,
        String orderId,
        PaymentStatus status
) {}