package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.ImageRepositoryPort;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Image;
import org.dtt.mscatalog.infrastructure.mapper.ImageMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ImageEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataImageRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaImageRepositoryAdapter implements ImageRepositoryPort {

    private final SpringDataImageRepository springDataImageRepository;
    private final ImageMapper imageMapper;
    @Override
    public Image save(Image image) {
        ImageEntity entity = imageMapper.toEntity(image);
        entity = springDataImageRepository.save(entity);
        return imageMapper.toDomain(entity);
    }

    @Override
    public Optional<Image> findById(UUID id) {
        return springDataImageRepository.findById(id).map(imageMapper::toDomain);
    }

    @Override
    public List<Image> findByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType) {
        return springDataImageRepository
                .findAllByOwnerIdAndOwnerType(ownerId, ownerType)
                .stream()
                .map(imageMapper::toDomain).toList();
    }

    @Override
    public void deleteByOwnerIdAndOwnerType(UUID ownerId, OwnerType ownerType) {
        springDataImageRepository.deleteByOwnerIdAndOwnerType(ownerId, ownerType);
    }


    @Override
    public void delete(UUID imageId) {
        springDataImageRepository.deleteById(imageId);
    }

    @Override
    public List<Image> findPrimaryImagesByOwnerIds(List<UUID> ownerIds, OwnerType ownerType) {
        return springDataImageRepository.findPrimaryImagesByOwnerIds(ownerIds, ownerType)
                .stream()
                .map(imageMapper::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Image> images) {
        springDataImageRepository.saveAll(
                images.stream().map(imageMapper::toEntity).toList()
        );
    }

}
