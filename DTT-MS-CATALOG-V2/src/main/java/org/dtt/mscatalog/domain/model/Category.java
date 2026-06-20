package org.dtt.mscatalog.domain.model;

import lombok.*;
import org.dtt.mscatalog.domain.exception.CategoryAlreadyDeletedException;
import org.dtt.mscatalog.domain.exception.CategoryAlreadyDisabledException;
import org.dtt.mscatalog.domain.exception.CategoryAlreadyEnabledException;
import org.dtt.mscatalog.domain.exception.InvalidSlugException;
import org.dtt.mscatalog.domain.exception.SelfParentCategoryException;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(includeFieldNames = true)
public class Category {

    @ToString.Include
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ToString.Include
    private String name;
    @ToString.Include
    private String slug;
    @ToString.Include
    private boolean deleted;
    @ToString.Include
    private CategoryStatus categoryStatus;
    private Category parent;
    @ToString.Include
    private String path;

    public void enable() {
        if (this.categoryStatus == CategoryStatus.ENABLED) {
            throw new CategoryAlreadyEnabledException();
        }
        this.categoryStatus = CategoryStatus.ENABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        if (this.categoryStatus == CategoryStatus.DISABLED) {
            throw new CategoryAlreadyDisabledException();
        }
        this.categoryStatus = CategoryStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if(this.deleted) {
            throw new CategoryAlreadyDeletedException();
        }
        this.deleted = true;
        this.categoryStatus = CategoryStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void refreshPath(Category parent) {
        String parentPath = (parent != null) ? parent.getPath() : "";
        this.path = parentPath + "/" + this.getSlug();
        this.updatedAt = LocalDateTime.now();
    }

    public void changeSlug(String newSlug) {
        if (newSlug == null || newSlug.isBlank()) {
            throw new InvalidSlugException(newSlug);
        }
        this.slug = newSlug;
        this.recalculatePath();
    }

    public void recalculatePath() {
        if (this.parent == null) {
            this.path = "/" + this.slug;
        } else {
            this.path = this.parent.getPath() + "/" + this.slug;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void setName(String name){
        if (name == null){
            return;
        }
        this.name = name;
    }

    public void setCategoryStatus(CategoryStatus categoryStatus){
        if (categoryStatus == null){
            return;
        }
        this.categoryStatus = categoryStatus;
    }

    public void setParent(Category parent) {
        if (parent != null && parent.getId() != null && parent.getId().equals(this.id)) {
            throw new SelfParentCategoryException();
        }
        this.parent = parent;
    }
}