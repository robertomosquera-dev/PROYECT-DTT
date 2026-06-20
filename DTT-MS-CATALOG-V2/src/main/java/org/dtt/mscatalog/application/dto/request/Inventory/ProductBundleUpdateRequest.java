package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;

public record ProductBundleUpdateRequest(
        @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
        String description,

        @Positive(message = "El precio debe ser mayor a 0")
        BigDecimal price,

        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) implements ISellableInventory {
    @Override
    public BigDecimal price() { return price; }
    @Override
    public Integer stock() { return stock; }
}
