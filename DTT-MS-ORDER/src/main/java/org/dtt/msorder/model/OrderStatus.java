package org.dtt.msorder.model;

import java.util.Set;

public enum OrderStatus {
    PENDING {
        @Override
        public Set<OrderStatus> nextStates() {
            return Set.of(COMPLETED, CANCELLED, FAILED);
        }
    },
    COMPLETED,
    CANCELLED,
    FAILED;

    public Set<OrderStatus> nextStates() {
        return Set.of();
    }

    public boolean canTransitionTo(OrderStatus nextState) {
        return nextStates().contains(nextState);
    }
}