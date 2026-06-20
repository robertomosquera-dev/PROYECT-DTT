package org.dtt.mscatalog.domain.exception;

public class ProductAlreadyEnabledException extends DomainException {
    public ProductAlreadyEnabledException() {
        super("Product is already enabled");
    }
}