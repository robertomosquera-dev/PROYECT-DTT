package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.exception.*;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String description;
    private ProductBaseStatus status;
    private boolean deleted;
    private Set<Category> categories;

    public void refreshCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (this.categories.contains(category)) {
            throw new CategoryAlreadyExistsException(category.getId());
        }
        this.categories.add(category);
    }
    public void enable() {
        if (this.status == ProductBaseStatus.ENABLED) {
            throw new ProductAlreadyEnabledException();
        }
        this.status = ProductBaseStatus.ENABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        if (this.status == ProductBaseStatus.DISABLED) {
            throw new ProductAlreadyDisabledException();
        }
        this.status = ProductBaseStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if(this.deleted) {
            throw new ProductAlreadyDeletedException(this.id);
        }
        this.deleted = true;
        this.status = ProductBaseStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            return;
        }
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeCategory(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        boolean removed = this.categories.removeIf(category -> category.getId().equals(categoryId));
        if (!removed) {
            throw new CategoryNotFoundInProductException(categoryId);
        }
    }
}
