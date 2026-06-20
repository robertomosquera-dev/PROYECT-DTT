package org.dtt.mscatalog.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.dtt.mscatalog.domain.exception.*;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


// Base de modelo de tipos de productos
// Con logica repetida para los hijo lo heredan BaseProductItem y ProductBundle
@Getter
@SuperBuilder
public abstract class BaseInventoryItem {
    private final UUID id;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer stock;
    private boolean deleted;
    private ProductStatus status;

    public void updateStock(Integer quantity) {
        updateValidateStock(quantity);
        this.stock = quantity;
    }

    private void updateValidateStock(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new InvalidStockException();
        }
    }

    public void activate() {
        if (this.status == ProductStatus.ACTIVE) {
            throw new ProductAlreadyActiveException();
        }
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == ProductStatus.INACTIVE) {
            throw new ProductAlreadyInactiveException();
        }
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if (this.deleted) {
            throw new InventoryItemAlreadyDeletedException();
        }
        this.deleted = true;
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void replenishStock(Integer quantity) {
        validateStock();
        this.stock += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdrawStock(Integer quantity) {
        validateStock();
        if (this.stock < quantity) {
            throw new InsufficientStockException();
        }
        this.stock -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    private void validateStock() {
        if (this.stock == null) {
            throw new StockNotInitializedException();
        }
    }

    protected void validatePrice(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new InvalidPriceException(fieldName + " cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException(fieldName + " cannot be negative");
        }
    }
}