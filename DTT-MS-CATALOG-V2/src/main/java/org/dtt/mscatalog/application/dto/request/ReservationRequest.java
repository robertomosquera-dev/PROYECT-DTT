package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
public record ReservationRequest(

        @NotNull(message = "El orderId es obligatorio")
        UUID orderId,

        @NotNull(message = "El userId es obligatorio")
        UUID userId,

        @NotEmpty(message = "La reserva debe contener al menos un item")
        List<@Valid ItemRequest> items

) {}