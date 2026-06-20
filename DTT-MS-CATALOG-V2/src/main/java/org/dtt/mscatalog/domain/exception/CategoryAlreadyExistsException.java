package org.dtt.mscatalog.domain.exception;

import java.util.UUID;

public class CategoryAlreadyExistsException extends DomainException {
    public CategoryAlreadyExistsException(UUID id) {
        super("Category with id " + id + " already exists in the product.");
    }
}