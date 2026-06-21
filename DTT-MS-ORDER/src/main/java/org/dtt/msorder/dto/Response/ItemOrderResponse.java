package org.dtt.msorder.dto.Response;

import lombok.Builder;
import org.dtt.msorder.model.ProductType;

import java.util.UUID;

@Builder
public record ItemOrderResponse(
        UUID id,
        String nombre,
        ProductType tipo,
        String imagenUrl
) {
}
