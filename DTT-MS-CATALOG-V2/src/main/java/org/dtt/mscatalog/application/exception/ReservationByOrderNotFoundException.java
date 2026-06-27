package org.dtt.mscatalog.application.exception;
import java.util.UUID;

public class ReservationByOrderNotFoundException extends RuntimeException {

    public ReservationByOrderNotFoundException(UUID orderId) {
        super("Reservation not found for order: " + orderId);
    }

}