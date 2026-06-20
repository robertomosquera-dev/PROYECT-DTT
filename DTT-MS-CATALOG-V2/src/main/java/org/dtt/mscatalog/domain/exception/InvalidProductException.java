package org.dtt.mscatalog.domain.exception;

public class InvalidProductException extends DomainException {
    public InvalidProductException() {
        super("Product cannot be null");
    }
}
