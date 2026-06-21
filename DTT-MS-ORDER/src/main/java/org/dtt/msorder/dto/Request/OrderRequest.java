package org.dtt.msorder.dto.Request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.dtt.msorder.model.Currency;

//Aqui se debe validar todos los atributos
@Schema(description = "Request used to create a new order")
public record OrderRequest(

        @Valid
        @NotEmpty
        @Schema(
                description = "List of items included in the order"
        )
        List<ItemRequest> listItem,

        @NotNull
        @Schema(
                description = "Platform that generated the order",
                example = "WEB"
        )
        String platform,

        @NotNull
        @Schema(
                description = "Currency used in the order"
        )
        Currency currency
) {
}
