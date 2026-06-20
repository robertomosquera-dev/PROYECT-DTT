package org.dtt.mscatalog.application.port.in.rentalProductUseCase;

import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.RentalProductResponse;

import java.util.UUID;

public interface UpdateRentalUseCase {
    RentalProductResponse update(UUID id, RentalProductUpdateRequest request);
}
