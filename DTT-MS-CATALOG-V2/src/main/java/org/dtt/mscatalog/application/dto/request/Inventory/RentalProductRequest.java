package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;
import java.util.UUID;

public record RentalProductRequest(
        @NotNull(message = "El productId es obligatorio")
        UUID productId,

        @NotNull(message = "El precio semanal es obligatorio")
        @Positive(message = "El precio semanal debe ser mayor a 0")
        BigDecimal weeklyPrice,

        @NotNull(message = "El precio mensual es obligatorio")
        @Positive(message = "El precio mensual debe ser mayor a 0")
        BigDecimal monthlyPrice,

        @NotNull(message = "El depósito de seguridad es obligatorio")
        @PositiveOrZero(message = "El depósito de seguridad debe ser mayor o igual a 0")
        BigDecimal securityDeposit,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) implements ISellableInventory {
    @Override
    public BigDecimal price() { return monthlyPrice; }
}
