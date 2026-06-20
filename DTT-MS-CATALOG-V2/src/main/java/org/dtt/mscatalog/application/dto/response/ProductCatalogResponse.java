package org.dtt.mscatalog.application.dto.response;


import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCatalogResponse(
    UUID id,
    String name,
    ProductType skuType,
    BigDecimal price,
    Integer stock,
    String categorySlugs,
    String url
) {
}
