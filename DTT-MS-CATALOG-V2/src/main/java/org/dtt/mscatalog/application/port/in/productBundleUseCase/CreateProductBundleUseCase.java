package org.dtt.mscatalog.application.port.in.productBundleUseCase;

import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.ProductBundleRequest;
import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;

import java.util.List;

public interface CreateProductBundleUseCase {

    ProductBundleResponse create(ProductBundleRequest request, List<ProductImgRequest> imgRequests);
}
