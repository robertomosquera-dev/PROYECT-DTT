package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.response.ProductResponse;

import java.util.UUID;

public interface RemoveImageFromProduct {
    ProductResponse removeImageFromProduct(UUID productId, UUID imageId);
}
