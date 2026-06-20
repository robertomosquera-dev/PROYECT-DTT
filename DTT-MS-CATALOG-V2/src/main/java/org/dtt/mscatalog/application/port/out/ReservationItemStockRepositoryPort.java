package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.domain.model.ReservationItemStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationItemStockRepositoryPort {
    ReservationItemStock save(ReservationItemStock reservationItemStock);
    Optional<ReservationItemStock> findById(UUID id);
    List<ReservationItemStock> saveAll(List<ReservationItemStock> reservationItemStocks);
}
