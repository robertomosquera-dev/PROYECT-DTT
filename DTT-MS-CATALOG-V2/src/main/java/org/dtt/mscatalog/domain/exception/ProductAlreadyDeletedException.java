package org.dtt.mscatalog.domain.exception;

import java.util.UUID;

public class ProductAlreadyDeletedException extends DomainException {
    public ProductAlreadyDeletedException(UUID id) {
        super("Product with id " + id + " has already been deleted.");
    }
}
