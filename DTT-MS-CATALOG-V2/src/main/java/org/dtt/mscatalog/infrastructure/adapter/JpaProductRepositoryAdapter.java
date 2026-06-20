package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.application.port.out.ProductRepositoryPort;
import org.dtt.mscatalog.domain.model.Product;
import org.dtt.mscatalog.infrastructure.mapper.ProductMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataProductRepository;
import org.dtt.mscatalog.infrastructure.persistence.repository.specification.ProductSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        entity = productRepository.save(entity);
        return productMapper.toDomain(entity);
    }

    @Override
    public List<Product> listProducts(ProductFilter filter, PageRequest pageRequest) {
        var productPage = productRepository.findAll(
                ProductSpecs.withFilter(filter),
                pageRequest
        );
        return productPage.getContent()
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository
                .findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return productRepository
                .findByName(name)
                .map(productMapper::toDomain);
    }

}
