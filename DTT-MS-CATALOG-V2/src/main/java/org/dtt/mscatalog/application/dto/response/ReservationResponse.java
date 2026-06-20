package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.StatusReservation;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationResponse(
        UUID orderId,
        UUID userId,
        UUID reservationId,
        StatusReservation status,
        List<ItemCatalogResponse> products
) {}