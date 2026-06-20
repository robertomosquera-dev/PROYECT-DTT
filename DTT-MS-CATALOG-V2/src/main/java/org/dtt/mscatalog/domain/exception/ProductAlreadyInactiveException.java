package org.dtt.mscatalog.domain.exception;

public class ProductAlreadyInactiveException extends DomainException {
    public ProductAlreadyInactiveException() {
        super("Product is now inactive");
    }
}
