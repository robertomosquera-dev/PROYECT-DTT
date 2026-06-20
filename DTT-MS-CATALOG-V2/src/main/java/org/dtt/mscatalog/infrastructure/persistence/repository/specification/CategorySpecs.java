package org.dtt.mscatalog.infrastructure.persistence.repository.specification;

import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CategorySpecs {

    public static Specification<CategoryEntity> withFilter(CategoryFilter filter) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            List<Predicate> predicates = new ArrayList<>();
            if (filter.parentId() != null) {
                predicates.add(cb.equal(root.get("parent").get("id"), filter.parentId()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("categoryStatus"), filter.status()));
            }

//            if (filter.productId() != null) {
//                predicates.add(cb.equal(root.join("products").get("id"), filter.productId()));
//            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}