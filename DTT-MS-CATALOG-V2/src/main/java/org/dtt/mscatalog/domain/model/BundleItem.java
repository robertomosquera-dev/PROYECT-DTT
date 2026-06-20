package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BundleItem {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductBundle bundle;
    private SaleProduct saleProduct;
    private Integer weight;

    public void updateWeight(Integer weight) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be greater than zero"
            );
        }

        this.weight = weight;
    }

    public void updateBundle(ProductBundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException(
                    "Bundle cannot be null"
            );
        }

        if (bundle.isDeleted()) {
            throw new IllegalArgumentException(
                    "Bundle cannot be deleted"
            );
        }

        if (bundle.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Bundle must be active"
            );
        }

        this.bundle = bundle;
    }

    public void updateSaleProduct(SaleProduct saleProduct) {
        if (saleProduct == null) {
            throw new IllegalArgumentException(
                    "Sale product cannot be null"
            );
        }

        if (saleProduct.isDeleted()) {
            throw new IllegalArgumentException(
                    "Sale product cannot be deleted"
            );
        }

        if (saleProduct.getStatus() != ProductStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Sale product must be active"
            );
        }

        this.saleProduct = saleProduct;
    }
}
