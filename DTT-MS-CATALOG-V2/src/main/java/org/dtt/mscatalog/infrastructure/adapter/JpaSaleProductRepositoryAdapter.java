package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.SaleProductRepositoryPort;
import org.dtt.mscatalog.domain.model.SaleProduct;
import org.dtt.mscatalog.infrastructure.mapper.SaleProductMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.SaleProductEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataSaleProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaSaleProductRepositoryAdapter implements SaleProductRepositoryPort {

    private final SpringDataSaleProductRepository springDataSaleProductRepository;
    private final SaleProductMapper saleProductMapper;

    @Override
    public boolean existsByProductId(UUID ProductId) {
        return springDataSaleProductRepository.existsByProductId(ProductId);
    }

    @Override
    public SaleProduct save(SaleProduct saleProduct) {
        SaleProductEntity entity = saleProductMapper.toEntity(saleProduct);
        entity = springDataSaleProductRepository.save(entity);
        return saleProductMapper.toDomain(entity);
    }

    @Override
    public void saveAll(List<SaleProduct> entities) {
        List<SaleProductEntity> saleProductEntities = saleProductMapper.toEntity(entities);
        springDataSaleProductRepository.saveAll(saleProductEntities);
    }

    @Override
    public Optional<SaleProduct> findById(UUID id) {
        return springDataSaleProductRepository
                .findById(id)
                .map(saleProductMapper::toDomain);
    }

    @Override
    public List<SaleProduct> findAllByIdsIn(List<UUID> ids) {
        List<SaleProductEntity> entities = springDataSaleProductRepository.findAllById(ids);
        return saleProductMapper.toDomain(entities);
    }

    @Override
    public List<SaleProduct> findAllByProductIdIn(Set<UUID> productIds) {
        List<SaleProductEntity> entities = springDataSaleProductRepository.findAllByIdIn(productIds);
        return entities.stream()
                .map(saleProductMapper::toDomain)
                .toList();
    }
}
