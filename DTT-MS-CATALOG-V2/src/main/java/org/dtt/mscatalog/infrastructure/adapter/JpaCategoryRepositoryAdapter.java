package org.dtt.mscatalog.infrastructure.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.application.port.out.CategoryRepositoryPort;
import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.infrastructure.mapper.CategoryMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.CategoryEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataCategoryRepository;
import org.dtt.mscatalog.infrastructure.persistence.repository.specification.CategorySpecs;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository
                .findById(id)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository
                .findBySlug(slug)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository
                .findByName(name)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findByPath(String path) {
        return categoryRepository
                .findByPath(path)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryMapper.toEntity(category);
        log.info("Entity id antes de save: {}", entity.getId());
        if (category.getParent() != null && category.getParent().getId() != null) {
            CategoryEntity parentRef = entityManager
                    .getReference(CategoryEntity.class, category.getParent().getId());
            entity.setParent(parentRef);
        }
        entity = categoryRepository.save(entity);
        return categoryMapper.toDomain(entity);
    }

    @Override
    public List<Category> listCategories(
            CategoryFilter filter,
            PageRequest pageRequest) {
        var page = categoryRepository.findAll(
                CategorySpecs.withFilter(filter),
                pageRequest
        );
        return page.getContent().stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public List<Category> findByParentId(UUID id) {
        return categoryRepository
                .findByParentId(id)
                .stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public Set<Category> findByIds(Set<UUID> ids) {
        return categoryRepository
                .findAllByIdIn(ids)
                .stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsBySlug(String slug) {
        return categoryRepository.existsBySlug(slug);
    }


}
