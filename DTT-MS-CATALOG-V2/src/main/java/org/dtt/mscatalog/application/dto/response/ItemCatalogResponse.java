package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ItemCatalogResponse(
        UUID productId,
        String title,
        String description,
        String category,
        Integer quantity,
        BigDecimal unitPrice,
        String pictureUrl
) {}