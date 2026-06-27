package org.dtt.mscatalog.domain.exception;

import org.dtt.mscatalog.domain.model.Enum.StatusReservation;

public class InvalidReservationStatusTransitionException extends RuntimeException {
    public InvalidReservationStatusTransitionException(
            StatusReservation current,
            StatusReservation target) {
        super(String.format(
                "Cannot change reservation status from %s to %s",
                current,
                target
        ));
    }
}