package org.dtt.mscatalog.domain.exception;

public class InvalidStockException extends DomainException {
    public InvalidStockException() {
        super("Stock cannot be zero or negative");
    }
}
