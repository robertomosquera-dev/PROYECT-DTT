package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;

public record SaleProductUpdateRequest(
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    BigDecimal price,

    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock
) implements ISellableInventory {}
