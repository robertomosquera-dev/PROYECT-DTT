package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.SaleProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SpringDataSaleProductRepository extends JpaRepository<SaleProductEntity, UUID> {
    boolean existsByProductId(UUID productId);

    List<SaleProductEntity> findAllByIdIn(Set<UUID> ids);
}
