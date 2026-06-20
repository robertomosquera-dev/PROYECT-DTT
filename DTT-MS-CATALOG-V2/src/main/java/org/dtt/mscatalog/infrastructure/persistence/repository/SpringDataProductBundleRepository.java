package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.ProductBundleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SpringDataProductBundleRepository extends JpaRepository<ProductBundleEntity, UUID> {
    List<ProductBundleEntity> findAllByIdIn(List<UUID> ids);
}
