package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.domain.model.ProductCatalog;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataProductCatalogRepository extends
        JpaRepository <ProductCatalogEntity, UUID> ,
        JpaSpecificationExecutor<ProductCatalogEntity> {
}
