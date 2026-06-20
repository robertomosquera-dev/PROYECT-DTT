package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.request.ProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.ProductResponse;

import java.util.UUID;

public interface UpdateProductUseCase {
    ProductResponse update(UUID id, ProductUpdateRequest productRequest);
}
