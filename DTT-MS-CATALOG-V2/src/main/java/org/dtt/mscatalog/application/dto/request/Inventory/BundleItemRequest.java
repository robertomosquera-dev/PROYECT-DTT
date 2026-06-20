package org.dtt.mscatalog.application.dto.request.Inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record BundleItemRequest(
        @NotNull(message = "El productSaleId es obligatorio")
        UUID productSaleId,

        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor a 0")
        Integer weight
) {
}
