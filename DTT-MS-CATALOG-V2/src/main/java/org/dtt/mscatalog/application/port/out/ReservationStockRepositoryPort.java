package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.domain.model.ReservationStock;
import java.util.Optional;
import java.util.UUID;

public interface ReservationStockRepositoryPort {
    ReservationStock save(ReservationStock reservationStock);
    Optional<ReservationStock> findById(UUID id);
}
