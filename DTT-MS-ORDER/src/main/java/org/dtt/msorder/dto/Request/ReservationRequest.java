package org.dtt.msorder.dto.Request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ReservationRequest(
        UUID orderId ,
        UUID userId,
        List<ItemRequest>items
) {
}
