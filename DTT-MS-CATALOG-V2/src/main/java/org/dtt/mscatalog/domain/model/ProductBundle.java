package org.dtt.mscatalog.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.dtt.mscatalog.domain.exception.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@SuperBuilder
public class ProductBundle extends BaseInventoryItem {
    private String name;
    private String description;
    private BigDecimal price;
    private Set<Category> categories;
    @Builder.Default
    private List<BundleItem> items = new ArrayList<>();
    private Integer rollsCount;

    public void updateName(String name) {
        if (name == null) return;
        if (name.isBlank()) throw new InvalidNameException("Name cannot be blank");
        if (name.length() < 3) throw new InvalidNameException("Name must be at least 3 characters");
        if (name.length() > 100) throw new InvalidNameException("Name cannot exceed 100 characters");
        this.name = name;
    }

    public void updateDescription(String description) {
        if (description == null) return;
        if (description.isBlank()) throw new InvalidDescriptionException("Description cannot be blank");
        if (description.length() > 500) throw new InvalidDescriptionException("Description cannot exceed 500 characters");
        this.description = description;
    }

    public void updatePrice(BigDecimal price) {
        if (price == null) return;
        validatePrice(price, "price");
        this.price = price;
    }

    public void updateRollsCount(Integer rollsCount) {
        if (rollsCount == null) return;
        if (rollsCount < 1) throw new InvalidRollsCountException("Rolls count must be at least 1");
        if (rollsCount > 100) throw new InvalidRollsCountException("Rolls count cannot exceed 100");
        this.rollsCount = rollsCount;
    }

    public void refreshCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {

        if (category == null) throw new IllegalArgumentException("Category cannot be null");

        if (this.categories.stream().anyMatch(c -> c.getId().equals(category.getId()))) {
            throw new CategoryAlreadyExistsException(category.getId());
        }

        this.categories.add(category);
    }

    public void removeCategory(UUID categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id cannot be null");
        }
        if(categories.stream().noneMatch(c -> c.getId().equals(categoryId))){
            throw new CategoryNotFoundInProductException(categoryId);
        }
        this.categories.removeIf(category -> category.getId().equals(categoryId));
    }

    public void refreshItems(List<BundleItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }

        boolean hasDuplicates = items.stream()
                .map(item -> item.getSaleProduct().getId())
                .distinct()
                .count() != items.size();

        if (hasDuplicates) {
            throw new IllegalArgumentException(
                    "Bundle cannot contain duplicate sale products"
            );
        }

        this.items = items;
    }


    public void addItem(BundleItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (items == null) {
            throw new IllegalStateException("Items collection is not initialized");
        }

        boolean alreadyExists = items.stream()
                .anyMatch(i -> i.getSaleProduct().getId()
                        .equals(item.getSaleProduct().getId()));

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "Sale product is already associated with this bundle"
            );
        }

        items.add(item);
    }

    public void removeItem(UUID bundleItemId) {
        if (bundleItemId == null) {
            throw new IllegalArgumentException("Bundle item id cannot be null");
        }

        if(items.stream().noneMatch(item -> bundleItemId.equals(item.getId()))){
            throw new IllegalArgumentException("Item with id " + bundleItemId + " not found in bundle");
        }

        items.removeIf(item -> bundleItemId.equals(item.getId()));
    }

}