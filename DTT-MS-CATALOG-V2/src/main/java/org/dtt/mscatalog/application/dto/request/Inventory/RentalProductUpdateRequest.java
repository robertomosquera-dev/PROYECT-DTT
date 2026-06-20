package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;

public record RentalProductUpdateRequest(
        @Positive(message = "El precio semanal debe ser mayor a 0")
        BigDecimal weeklyPrice,

        @Positive(message = "El precio mensual debe ser mayor a 0")
        BigDecimal monthlyPrice,

        @PositiveOrZero(message = "El depósito de seguridad debe ser mayor o igual a 0")
        BigDecimal securityDeposit,

        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) implements ISellableInventory {
    @Override
    public BigDecimal price() { return monthlyPrice; }
}

