package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.request.ProductRequest;
import org.dtt.mscatalog.application.dto.response.ProductResponse;

public interface CreateProductUseCase {
    ProductResponse createProduct(ProductRequest productRequest);
}
