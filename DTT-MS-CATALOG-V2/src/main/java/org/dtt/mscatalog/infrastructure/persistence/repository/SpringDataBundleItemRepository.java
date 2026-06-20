package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.BundleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBundleItemRepository extends JpaRepository<BundleItemEntity, UUID> {

}
