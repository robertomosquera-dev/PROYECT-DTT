package org.dtt.msorder.dto.Response;


import lombok.Builder;
import org.dtt.msorder.model.StatusReservation;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationResponse(
        UUID orderId,
        UUID userId,
        UUID reservationId,
        StatusReservation status,
        List<ItemCatalogResponse> products
) {
}
