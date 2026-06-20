package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.BundleItemRepositoryPort;
import org.dtt.mscatalog.infrastructure.mapper.ProductBundleMapper;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataBundleItemRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBundleItemRepositoryAdapter implements BundleItemRepositoryPort {

    private final SpringDataBundleItemRepository repository;
    private final ProductBundleMapper mapper;

    public void deleteById(UUID id){
        if(!repository.existsById(id)){
            throw new RuntimeException("BundleItem not found");
        }
        repository.deleteById(id);
    }
}
