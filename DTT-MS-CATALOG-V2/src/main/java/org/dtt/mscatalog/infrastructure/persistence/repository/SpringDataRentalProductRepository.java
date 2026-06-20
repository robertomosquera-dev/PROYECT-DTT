package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.RentalProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataRentalProductRepository extends JpaRepository<RentalProductEntity, UUID> {
    boolean existsByProductId(UUID productId);
}
