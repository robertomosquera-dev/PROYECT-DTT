package org.dtt.mscatalog.application.port.in.inventoryUseCase;

import java.util.UUID;

public interface ReplenishStockUseCase {
    void replenishStock(UUID id, Integer quantity);
}
