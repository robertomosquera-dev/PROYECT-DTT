package org.dtt.mscatalog.application.dto.response;


import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.util.UUID;

@Builder
public record ItemOrderResponse(
        UUID id,
        String name,
        ProductType type,
        String imageUrl
) {
}
