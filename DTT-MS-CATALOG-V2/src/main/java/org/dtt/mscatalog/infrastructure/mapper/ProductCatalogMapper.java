package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.response.ProductCatalogResponse;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCatalogMapper {
    ProductCatalogResponse toResponse(ProductCatalogEntity productCatalogEntity);
    List<ProductCatalogResponse> toResponse(List<ProductCatalogEntity> productCatalogEntities);
}
