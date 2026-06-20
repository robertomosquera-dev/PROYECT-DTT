package org.dtt.mscatalog.infrastructure.persistence.repository.specification;

import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ProductCatalogSpecs {

    public static Specification<ProductCatalogEntity> withFilters(
            ProductCatalogFilter filter
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.skuType() != null) {
                predicates.add(cb.equal(root.get("skuType"), filter.skuType()));
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.categorySlug() != null && !filter.categorySlug().isEmpty()) {

                for (String slug : filter.categorySlug()) {
                    predicates.add(
                            cb.like(
                                    root.get("categorySlugs"),
                                    "%" + slug + "%"
                            )
                    );
                }
            }

            if (filter.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
            }

            if (filter.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}