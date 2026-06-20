package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.RentalProductRepositoryPort;
import org.dtt.mscatalog.domain.model.RentalProduct;
import org.dtt.mscatalog.infrastructure.mapper.RentalProductMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.RentalProductEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataRentalProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class JpaRentalProductRepository implements RentalProductRepositoryPort {

    private final SpringDataRentalProductRepository springDataRentalProductRepository;
    private final RentalProductMapper rentalProductMapper;

    @Override
    public boolean existsByProductId(UUID id) {
        return springDataRentalProductRepository.existsByProductId(id);
    }

    @Override
    public RentalProduct save(RentalProduct rentalProduct) {
        RentalProductEntity product = rentalProductMapper.toEntity(rentalProduct);
        product = springDataRentalProductRepository.save(product);
        return rentalProductMapper.toDomain(product);
    }

    @Override
    public void saveAll(List<RentalProduct> entities) {
        List<RentalProductEntity> rentalProductEntities = rentalProductMapper.toEntity(entities);
        springDataRentalProductRepository.saveAll(rentalProductEntities);
    }

    @Override
    public Optional<RentalProduct> findById(UUID id) {
        return springDataRentalProductRepository.findById(id).map(rentalProductMapper::toDomain);
    }

    @Override
    public List<RentalProduct> findAllByIdsIn(List<UUID> ids) {
        List<RentalProductEntity> entities = springDataRentalProductRepository.findAllById(ids);
        return rentalProductMapper.toDomain(entities);
    }

}
