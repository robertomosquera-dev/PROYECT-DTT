package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_products")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class SaleProductEntity extends EntityBase {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private ProductEntity product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductStatus status;

    private boolean deleted;

    @Override
    protected void onPrePersist() {
        if (this.status == null) {
            this.status = ProductStatus.ACTIVE;
        }
    }
}