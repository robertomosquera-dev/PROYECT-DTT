package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;

import java.util.UUID;

@Entity
@Table(
        name = "images",
        indexes = {
                @Index(name = "idx_images_owner_id_type_new", columnList = "owner_id, owner_type")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ImageEntity extends EntityBase {

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private OwnerType ownerType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "img_order")
    private Short order;
}