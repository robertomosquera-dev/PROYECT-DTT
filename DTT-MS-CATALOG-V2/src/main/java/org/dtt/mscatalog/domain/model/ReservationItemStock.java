package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.model.Enum.ProductType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationItemStock {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ReservationStock reservationStock;
    private UUID productId;
    private ProductType type;
    private Integer quantity;

}
