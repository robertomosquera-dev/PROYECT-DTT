package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Image;
import org.dtt.mscatalog.infrastructure.persistence.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataImageRepository extends JpaRepository<ImageEntity, UUID> {

    List<ImageEntity> findByOwnerId(UUID ownerId);

    List<ImageEntity> findAllByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    void deleteByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    @Query(value = "SELECT * FROM images WHERE owner_id IN :ownerIds AND owner_type = :#{#ownerType.name()} AND img_order = 0", nativeQuery = true)
    List<ImageEntity> findPrimaryImagesByOwnerIds(@Param("ownerIds") List<UUID> ownerIds, @Param("ownerType") OwnerType ownerType);
}
