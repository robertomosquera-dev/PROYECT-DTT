package org.dtt.mscatalog.application.port.in.reservationStockUseCase;

import java.util.UUID;

public interface ProcessReservationUseCase {
    void processReservation(UUID reservationId, Boolean isConfirmed);
}
