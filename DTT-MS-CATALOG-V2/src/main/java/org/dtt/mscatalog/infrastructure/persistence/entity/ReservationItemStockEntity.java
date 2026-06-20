package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.util.UUID;

@Entity
@Table(name = "reservation_item_stock")
@Getter
@Setter
@NoArgsConstructor
public class ReservationItemStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationStockEntity reservation;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductType type;

    @Column(nullable = false)
    private Integer quantity;
}