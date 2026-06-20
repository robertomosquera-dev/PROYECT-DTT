package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.ProductBundleRepositoryPort;
import org.dtt.mscatalog.domain.model.BundleItem;
import org.dtt.mscatalog.domain.model.ProductBundle;
import org.dtt.mscatalog.infrastructure.mapper.CategoryMapper;
import org.dtt.mscatalog.infrastructure.mapper.ProductBundleMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.BundleItemEntity;
import org.dtt.mscatalog.infrastructure.persistence.entity.CategoryEntity;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductBundleEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataProductBundleRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaProductBundleRepositoryAdapter implements ProductBundleRepositoryPort {

    private final SpringDataProductBundleRepository repository;
    private final ProductBundleMapper mapper;
    private final CategoryMapper categoryMapper;

    @Override
    public Optional<ProductBundle> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductBundle> findAllByIdsIn(List<UUID> ids) {
        return mapper.toDomain(repository.findAllByIdIn(ids));
    }

    @Override
    public ProductBundle save(ProductBundle bundle) {
        ProductBundleEntity entity = bundle.getId() != null
                ? repository.findById(bundle.getId()).orElseGet(ProductBundleEntity::new)
                : new ProductBundleEntity();

        entity.setName(bundle.getName());
        entity.setDescription(bundle.getDescription());
        entity.setPrice(bundle.getPrice());
        entity.setStock(bundle.getStock());
        entity.setStatus(bundle.getStatus());
        entity.setRollsCount(bundle.getRollsCount());
        entity.setDeleted(bundle.isDeleted());

        entity.setCategories(
                bundle.getCategories().stream()
                        .map(categoryMapper::toEntity)
                        .collect(Collectors.toSet())
        );

        Set<UUID> domainItemIds = bundle.getItems().stream()
                .map(BundleItem::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        entity.getItems().removeIf(i -> !domainItemIds.contains(i.getId()));

        bundle.getItems().stream()
                .filter(i -> i.getId() == null)
                .forEach(i -> {
                    BundleItemEntity itemEntity = mapper.toEntity(i);
                    itemEntity.setBundle(entity);
                    entity.getItems().add(itemEntity);
                });

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void saveAll(List<ProductBundle> bundles) {
        bundles.forEach(this::save);
    }

    @Override
    public boolean existsByProductId(UUID productId) {
        return false;
    }
}