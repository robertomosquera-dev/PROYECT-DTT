package org.dtt.mscatalog.application.exception;

public class ProductAlreadyInSaleException extends RuntimeException {
    public ProductAlreadyInSaleException() {
        super("Product already in sale");
    }
}
