package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;
import org.hibernate.annotations.Immutable;
import org.dtt.mscatalog.domain.model.Enum.ProductType; // Tu nuevo Enum
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "vista_catalogo")
@Immutable
@Getter
public class ProductCatalogEntity {

    @Id
    private UUID id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sku_type")
    private ProductType skuType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status;

    private BigDecimal price;

    private Integer stock;

    private String categorySlugs;

    private String url;
}