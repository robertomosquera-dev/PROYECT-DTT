package org.dtt.mscatalog.infrastructure.persistence.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecs {

    public static Specification<ProductEntity> withFilter(ProductFilter filter) {
        return (root, query, cb) -> {

            if (Long.class != query.getResultType()) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
                var categoryJoin = root.join("categories");
                predicates.add(categoryJoin.get("id").in(filter.categoryIds()));
            }

            if (filter.categorySlugs() != null && !filter.categorySlugs().isEmpty()) {
                var categoryJoin = root.join("categories");
                predicates.add(categoryJoin.get("slug").in(filter.categorySlugs()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}