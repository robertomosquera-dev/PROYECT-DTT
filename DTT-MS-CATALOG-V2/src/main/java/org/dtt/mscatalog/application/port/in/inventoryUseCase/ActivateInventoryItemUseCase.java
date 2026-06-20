package org.dtt.mscatalog.application.port.in.inventoryUseCase;

import java.util.UUID;

public interface ActivateInventoryItemUseCase {
    void activateProduct(UUID id);
}
