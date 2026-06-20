package org.dtt.mscatalog.domain.exception;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException() {
        super("Insufficient stock to carry out the operation");
    }
}
