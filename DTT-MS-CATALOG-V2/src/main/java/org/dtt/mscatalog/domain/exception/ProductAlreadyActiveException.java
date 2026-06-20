package org.dtt.mscatalog.domain.exception;

public class ProductAlreadyActiveException extends DomainException {
    public ProductAlreadyActiveException() {
        super("Product is now active");
    }
}
