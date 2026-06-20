package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataReservationStockRepository extends JpaRepository<ReservationStockEntity, UUID> {
}
