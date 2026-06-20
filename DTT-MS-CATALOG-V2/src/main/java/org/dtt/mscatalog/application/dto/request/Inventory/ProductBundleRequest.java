package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.dtt.mscatalog.application.dto.ISellableInventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public record ProductBundleRequest(
        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a 0")
        BigDecimal price,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
        String description,

        @NotEmpty(message = "Debe asignar al menos una categoría")
        Set<UUID> categories,

        @NotEmpty(message = "El bundle debe tener al menos un item")
        @Valid
        List<BundleItemRequest> items,

        @NotNull(message = "El rollsCount es obligatorio")
        @Min(value = 1, message = "El rollsCount debe ser al menos 1")
        Integer rollsCount
) implements ISellableInventory {
    @Override
    public BigDecimal price() { return price; }
}
