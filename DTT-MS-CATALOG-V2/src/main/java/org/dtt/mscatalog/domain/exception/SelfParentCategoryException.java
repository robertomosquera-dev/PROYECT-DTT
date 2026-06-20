package org.dtt.mscatalog.domain.exception;

public class SelfParentCategoryException extends DomainException {
    public SelfParentCategoryException() {
        super("A category cannot be its own parent");
    }
}