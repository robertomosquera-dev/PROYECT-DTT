package org.dtt.mscatalog.domain.exception;

public class CategoryAlreadyDeletedException extends DomainException {
    public CategoryAlreadyDeletedException() {
        super("Category is already deleted");
    }
}