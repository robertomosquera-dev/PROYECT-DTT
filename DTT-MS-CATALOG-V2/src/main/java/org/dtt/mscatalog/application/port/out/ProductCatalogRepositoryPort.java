package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface ProductCatalogRepositoryPort {
    List<ProductCatalogEntity> findAll(ProductCatalogFilter filter, PageRequest pageRequest);
    List<ProductCatalogEntity> findAllByIds(List<UUID> ids);
}
