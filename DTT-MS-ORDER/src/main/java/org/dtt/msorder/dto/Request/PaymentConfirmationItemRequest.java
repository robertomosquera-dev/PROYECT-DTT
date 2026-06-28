package org.dtt.msorder.dto.Request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentConfirmationItemRequest(
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}