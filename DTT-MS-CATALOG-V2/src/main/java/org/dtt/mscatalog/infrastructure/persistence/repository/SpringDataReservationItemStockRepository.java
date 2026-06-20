package org.dtt.mscatalog.infrastructure.persistence.repository;

import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationItemStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataReservationItemStockRepository extends JpaRepository<ReservationItemStockEntity, UUID> {
}
