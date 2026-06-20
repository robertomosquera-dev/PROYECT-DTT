package org.dtt.mscatalog.domain.model;

import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.model.Enum.ContractStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RentalContract {
    private final UUID id;
    private final UUID rentalProductId;
    private final UUID clientId;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private ContractStatus status;
    private final Integer quantity;

    public void transitionTo(ContractStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Transición inválida: No se puede cambiar el contrato de " + this.status + " a " + newStatus
            );
        }
        this.status = newStatus;
    }

    public boolean overlapsWith(LocalDateTime start, LocalDateTime end) {
        return !startDate.isAfter(end) && !endDate.isBefore(start);
    }
}