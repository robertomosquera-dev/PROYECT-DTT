package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleProductRequest(
        @NotNull(message = "El ID del producto es obligatorio")
        UUID productId,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        BigDecimal price,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock
) implements ISellableInventory {}