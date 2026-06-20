package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.model.Enum.StatusReservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationStock {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID userId;
    private UUID orderId;
    private StatusReservation estado;

    @Builder.Default
    private List<ReservationItemStock> items = new ArrayList<>();

    public void addItems(List<ReservationItemStock> reservationItems) {
        this.items.addAll(reservationItems);
    }

    public List<UUID> getProductsId(){
        return items.stream().map(ReservationItemStock::getProductId).toList();
    }

    public void changeStatus(StatusReservation status){
        if(this.estado.canTransitionTo(status)){
            this.estado = status;
        }else{
            throw new IllegalStateException("Invalid status transition");
        }
    }
}
