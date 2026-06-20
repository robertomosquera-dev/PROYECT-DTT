package org.dtt.mscatalog.application.port.out;

import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.domain.model.Category;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Optional<Category> findById(UUID id);
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByName(String name);
    Optional<Category> findByPath(String path);
    Category save(Category category);
    List<Category> listCategories(CategoryFilter filter, PageRequest pageRequest);
    List<Category> findByParentId(UUID id);
    Set<Category> findByIds(Set<UUID> ids);
    boolean existsBySlug(String slug);
}
