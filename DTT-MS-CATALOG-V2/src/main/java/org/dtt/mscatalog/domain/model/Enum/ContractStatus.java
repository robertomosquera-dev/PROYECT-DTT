package org.dtt.mscatalog.domain.model.Enum;

import java.util.Set;

public enum ContractStatus {

    PENDING {
        @Override
        public Set<ContractStatus> nextStates() {
            return Set.of(ACTIVE, CANCELLED);
        }
    },
    ACTIVE {
        @Override
        public Set<ContractStatus> nextStates() {
            return Set.of(COMPLETED, OVERDUE);
        }
    },
    OVERDUE {
        @Override
        public Set<ContractStatus> nextStates() {
            return Set.of(COMPLETED);
        }
    },
    COMPLETED,
    CANCELLED;

    public Set<ContractStatus> nextStates() {
        return Set.of();
    }

    public boolean canTransitionTo(ContractStatus next) {
        return nextStates().contains(next);
    }
}