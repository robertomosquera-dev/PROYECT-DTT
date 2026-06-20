package org.dtt.mscatalog.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_path", columnList = "path"),
        @Index(name = "idx_category_parent", columnList = "parent_id")
})
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
@AllArgsConstructor
@Builder
public class CategoryEntity extends EntityBase {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CategoryStatus categoryStatus = CategoryStatus.ENABLED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, length = 500)
    private String path;

    @Override
    protected void onPrePersist() {
        if (this.categoryStatus == null) {
            this.categoryStatus = CategoryStatus.ENABLED;
        }
    }
}