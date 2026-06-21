package org.dtt.msmercadopago.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record WebhookOrderData(
        String orderId,
        String status,
        Map<String, String> metadata,
        List<PaymentData> payments, // 👈 NUEVO
        BigDecimal totalAmount
) {}