package org.dtt.mscatalog.application.dto;

import org.dtt.mscatalog.domain.model.Enum.ProductStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.math.BigDecimal;
import java.util.List;

public record ProductCatalogFilter(
        ProductType skuType,
        List<String> categorySlug,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductStatus status
) {
}
