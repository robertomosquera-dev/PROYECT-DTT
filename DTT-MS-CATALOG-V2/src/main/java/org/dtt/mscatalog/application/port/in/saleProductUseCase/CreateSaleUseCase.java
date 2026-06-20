package org.dtt.mscatalog.application.port.in.saleProductUseCase;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.SaleProductResponse;

import java.util.UUID;

public interface CreateSaleUseCase {
    SaleProductResponse create(SaleProductRequest request);
}
