package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Image;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageRepositoryPort {

    Image save(Image image);

    Optional<Image> findById(UUID id);

    List<Image> findByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    void deleteByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType);

    void saveAll(List<Image> images);

    void delete(UUID imageId);

    List<Image> findPrimaryImagesByOwnerIds(List<UUID> ownerIds, OwnerType ownerType);


}
