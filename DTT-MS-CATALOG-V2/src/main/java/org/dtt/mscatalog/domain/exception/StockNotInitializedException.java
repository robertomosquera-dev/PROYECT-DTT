package org.dtt.mscatalog.domain.exception;

public class StockNotInitializedException extends DomainException {
    public StockNotInitializedException() {
        super("Stock has not been initialized");
    }
}
