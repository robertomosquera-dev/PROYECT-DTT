package org.dtt.mscatalog.domain.exception;

public class CategoryAlreadyDisabledException extends DomainException {
    public CategoryAlreadyDisabledException() {
        super("Category is already disabled");
    }
}