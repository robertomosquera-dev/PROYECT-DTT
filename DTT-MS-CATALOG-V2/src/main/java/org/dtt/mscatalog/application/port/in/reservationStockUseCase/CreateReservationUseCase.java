package org.dtt.mscatalog.application.port.in.reservationStockUseCase;

import org.dtt.mscatalog.application.dto.request.ReservationRequest;
import org.dtt.mscatalog.application.dto.response.ReservationResponse;

public interface CreateReservationUseCase {
    ReservationResponse createReservation(ReservationRequest request);
}
