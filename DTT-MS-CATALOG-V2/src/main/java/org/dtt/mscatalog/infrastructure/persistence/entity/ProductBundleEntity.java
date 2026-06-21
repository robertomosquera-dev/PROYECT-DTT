package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_bundles")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class ProductBundleEntity extends EntityBase {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductStatus status;

    private boolean deleted;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bundle_categories",
            joinColumns = @JoinColumn(name = "bundle_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> categories = new HashSet<>();

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundleItemEntity> items = new ArrayList<>();

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "rolls_count", nullable = false)
    private Integer rollsCount;

    @Override
    protected void onPrePersist() {
        if (this.status == null) {
            this.status = ProductStatus.ACTIVE;
        }
    }
}