package org.dtt.mscatalog.application.port.in.inventoryUseCase;

import java.util.UUID;

public interface DeactivateInventoryItemUseCase {
    void deactivateProduct(UUID id);
}
