package org.dtt.mscatalog.application.port.in.saleProductUseCase;

import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.SaleProductResponse;

import java.util.UUID;

public interface UpdateSaleUseCase {
    SaleProductResponse update(UUID id, SaleProductUpdateRequest request);
}
