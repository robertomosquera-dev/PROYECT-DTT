package org.dtt.mscatalog.domain.model.Enum;

import java.util.Set;

public enum StatusReservation {
    COMPLETE {},
    CANCELED {},
    PENDING {
        @Override
        public Set<StatusReservation> nextStates() {
            return Set.of(COMPLETE, CANCELED);
        }
    };

    public Set<StatusReservation> nextStates() {
        return Set.of();
    }

    public boolean canTransitionTo(StatusReservation next) {
        return nextStates().contains(next);
    }
}