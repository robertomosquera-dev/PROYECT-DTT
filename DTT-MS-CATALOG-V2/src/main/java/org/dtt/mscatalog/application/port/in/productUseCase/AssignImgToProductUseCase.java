package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.ProductResponse;

import java.util.UUID;

public interface AssignImgToProductUseCase {
    ProductResponse assignImgToProduct(UUID id, ProductImgRequest productImgRequest);
}
