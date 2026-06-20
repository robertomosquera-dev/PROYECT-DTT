package org.dtt.mscatalog.domain.exception;

public class InventoryItemAlreadyDeletedException extends DomainException {
    public InventoryItemAlreadyDeletedException() {
        super("Inventory item has already been removed");
    }
}
