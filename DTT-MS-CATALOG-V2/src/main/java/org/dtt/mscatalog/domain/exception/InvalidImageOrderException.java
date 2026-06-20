package org.dtt.mscatalog.domain.exception;

public class InvalidImageOrderException extends DomainException {
    public InvalidImageOrderException() {
        super("Image order cannot be null");
    }
}