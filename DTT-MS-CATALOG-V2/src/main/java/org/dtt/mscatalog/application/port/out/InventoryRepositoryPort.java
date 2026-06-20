package org.dtt.mscatalog.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort<T> {
    Optional<T> findById(UUID id);
    List<T> findAllByIdsIn(List<UUID> ids);
    T save(T entity);
    void saveAll(List<T> entities);
    boolean existsByProductId(UUID productId);
}