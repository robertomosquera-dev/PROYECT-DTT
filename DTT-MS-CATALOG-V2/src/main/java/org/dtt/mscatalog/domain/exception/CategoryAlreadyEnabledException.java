package org.dtt.mscatalog.domain.exception;

public class CategoryAlreadyEnabledException extends DomainException {
    public CategoryAlreadyEnabledException() {
        super("Category is already enabled");
    }
}