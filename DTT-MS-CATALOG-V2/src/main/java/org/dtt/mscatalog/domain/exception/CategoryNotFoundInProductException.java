package org.dtt.mscatalog.domain.exception;

import java.util.UUID;

public class CategoryNotFoundInProductException extends DomainException {
    public CategoryNotFoundInProductException(UUID id) {
        super("Category with id " + id + " not found in the product.");
    }
}
