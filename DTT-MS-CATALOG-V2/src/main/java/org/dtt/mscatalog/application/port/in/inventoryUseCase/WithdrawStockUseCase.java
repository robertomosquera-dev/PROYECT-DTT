package org.dtt.mscatalog.application.port.in.inventoryUseCase;

import java.util.UUID;

public interface WithdrawStockUseCase {
    void withdrawStock(UUID id, Integer quantity);
}
