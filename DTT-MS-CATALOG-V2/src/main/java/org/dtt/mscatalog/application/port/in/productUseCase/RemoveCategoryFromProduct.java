package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.response.ProductResponse;

import java.util.UUID;

public interface RemoveCategoryFromProduct {
    ProductResponse removeCategoryFromProduct(UUID productId, UUID categoryId);
}
