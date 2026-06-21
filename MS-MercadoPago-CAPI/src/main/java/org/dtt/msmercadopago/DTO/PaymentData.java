package org.dtt.msmercadopago.DTO;

import java.math.BigDecimal;

public record PaymentData(
        String status,
        BigDecimal totalPaidAmount
) {}
