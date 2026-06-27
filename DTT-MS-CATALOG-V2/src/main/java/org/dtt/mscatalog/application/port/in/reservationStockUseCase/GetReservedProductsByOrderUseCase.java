package org.dtt.mscatalog.application.port.in.reservationStockUseCase;

import org.dtt.mscatalog.application.dto.response.ItemOrderResponse;

import java.util.List;
import java.util.UUID;

public interface GetReservedProductsByOrderUseCase {
    List<ItemOrderResponse> getReservedProducts(UUID orderId);
}
