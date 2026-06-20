package org.dtt.mscatalog.application.port.in.inventoryUseCase;

import org.dtt.mscatalog.application.dto.request.ItemRequest;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.domain.model.ReservationStock;

import java.util.List;

public interface ProcessStockUseCase {
    List<ReservationItemStock> processStock(List<ItemRequest> requests,ReservationStock reservation);
}
