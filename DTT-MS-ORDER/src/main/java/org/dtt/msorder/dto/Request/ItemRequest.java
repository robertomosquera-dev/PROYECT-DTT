package org.dtt.msorder.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.dtt.msorder.model.ProductType;

import java.util.UUID;

@Schema(description = "Product item included in the order")
public record ItemRequest(

        @NotNull
        @Schema(
                description = "Product identifier"
        )
        UUID productId,

        @NotNull
        @Schema(
                description = "Type of product"
        )
        ProductType type,

        @NotNull
        @Min(1)
        @Schema(
                description = "Quantity requested",
                example = "2"
        )
        Integer quantity
) {
}
