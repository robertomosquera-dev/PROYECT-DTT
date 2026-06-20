package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.infrastructure.persistence.entity.CategoryEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.specification.CategorySpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCategoryRepository extends
        JpaRepository<CategoryEntity, UUID>,
        JpaSpecificationExecutor<CategoryEntity> {
    Optional<CategoryEntity> findBySlug(String slug);
    Optional<CategoryEntity> findByName(String name);
    Optional<CategoryEntity> findByPath(String path);

    List<CategoryEntity> findByParentId(UUID parentId);

    boolean existsBySlug(String slug);

    List<CategoryEntity> findAllByIdIn(Collection<UUID> ids);
}
