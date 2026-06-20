package org.dtt.mscatalog.application.port.in.productBundleUseCase;

import org.dtt.mscatalog.application.dto.request.Inventory.ProductBundleUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;

import java.util.UUID;

public interface UpdateProductBundleUseCase {

    ProductBundleResponse update(
            UUID id,
            ProductBundleUpdateRequest request
    );
}
