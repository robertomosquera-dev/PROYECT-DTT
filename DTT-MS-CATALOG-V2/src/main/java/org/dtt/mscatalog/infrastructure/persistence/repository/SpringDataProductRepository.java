package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataProductRepository extends
        JpaRepository<ProductEntity, UUID>,
        JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findByName(String name);

}
