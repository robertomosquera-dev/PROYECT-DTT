package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class ProductEntity extends EntityBase {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProductBaseStatus status = ProductBaseStatus.ENABLED;

    private boolean deleted = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> categories = new HashSet<>();

    @Override
    protected void onPrePersist() {
        if (this.status == null) {
            this.status = ProductBaseStatus.ENABLED;
        }
    }

}