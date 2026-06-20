package org.dtt.mscatalog.application.port.in.rentalProductUseCase;

import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.RentalProductResponse;

public interface CreateRentalUseCase {
    RentalProductResponse create(RentalProductRequest request);
}
