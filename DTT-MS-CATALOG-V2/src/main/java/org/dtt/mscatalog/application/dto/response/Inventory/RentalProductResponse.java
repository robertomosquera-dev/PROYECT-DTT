package org.dtt.mscatalog.application.dto.response.Inventory;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.response.ProductImgResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RentalProductResponse(
        UUID id,
        UUID productId,
        BigDecimal weeklyPrice,
        BigDecimal monthlyPrice,
        BigDecimal securityDeposit,
        Integer stock,
        List<ProductImgResponse> images
) implements ISellableInventory {
    @Override
    public BigDecimal price() {
        return monthlyPrice;
    }
}
