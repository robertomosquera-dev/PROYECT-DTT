package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.util.UUID;

public record ItemRequest(

        @NotNull(message = "El productId es obligatorio")
        UUID productId,

        @NotNull(message = "El tipo de producto es obligatorio")
        ProductType type,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer quantity

) {
}