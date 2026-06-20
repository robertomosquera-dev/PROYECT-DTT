package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.dtt.mscatalog.domain.model.Enum.StatusReservation; // Asegúrate de tener este Enum
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservations_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationStockEntity extends EntityBase {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusReservation estado;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationItemStockEntity> items = new ArrayList<>();


    @Override
    protected void onPrePersist() {
        if (this.estado == null) {
            this.estado = StatusReservation.PENDING;
        }
    }
}