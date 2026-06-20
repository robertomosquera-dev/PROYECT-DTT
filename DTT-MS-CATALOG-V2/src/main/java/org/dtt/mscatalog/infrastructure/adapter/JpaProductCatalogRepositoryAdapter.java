package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.application.port.out.ProductCatalogRepositoryPort;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataProductCatalogRepository;
import org.dtt.mscatalog.infrastructure.persistence.repository.specification.ProductCatalogSpecs;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaProductCatalogRepositoryAdapter implements ProductCatalogRepositoryPort {

    private final SpringDataProductCatalogRepository springDataProductCatalogRepository;


    public List<ProductCatalogEntity> findAll(ProductCatalogFilter filter, PageRequest pageRequest) {
        return springDataProductCatalogRepository
                .findAll(ProductCatalogSpecs.withFilters(filter), pageRequest)
                .getContent();
    }

    @Override
    public List<ProductCatalogEntity> findAllByIds(List<UUID> ids) {
        return springDataProductCatalogRepository.findAllById(ids);
    }

}
