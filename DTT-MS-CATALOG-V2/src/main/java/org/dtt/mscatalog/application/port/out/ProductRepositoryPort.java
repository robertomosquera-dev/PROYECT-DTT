package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.domain.model.Product;

import org.springframework.data.domain.PageRequest;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    List<Product> listProducts(ProductFilter filter, PageRequest pageRequest);
    Optional<Product> findById(UUID id);
    Optional<Product> findByName(String name);
}
