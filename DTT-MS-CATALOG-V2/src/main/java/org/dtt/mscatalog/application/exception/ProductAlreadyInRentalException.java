package org.dtt.mscatalog.application.exception;

public class ProductAlreadyInRentalException extends RuntimeException {
    public ProductAlreadyInRentalException() {
        super("Product already in rental");
    }
}