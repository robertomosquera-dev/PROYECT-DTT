package org.dtt.mscatalog.application.port.in.productBundleUseCase;

import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;

import java.util.UUID;

public interface AssignImgToBundleUserCase {
    ProductBundleResponse assignImgToBundle(UUID id, ProductImgRequest imageRequest);
}
