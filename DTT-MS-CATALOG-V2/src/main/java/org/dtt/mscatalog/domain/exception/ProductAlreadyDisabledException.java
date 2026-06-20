package org.dtt.mscatalog.domain.exception;

public class ProductAlreadyDisabledException extends DomainException {
    public ProductAlreadyDisabledException() {
        super("Product is already disabled");
    }
}