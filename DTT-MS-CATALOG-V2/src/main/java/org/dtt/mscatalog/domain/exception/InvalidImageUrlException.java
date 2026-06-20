// domain/exception/InvalidImageUrlException.java
package org.dtt.mscatalog.domain.exception;

public class InvalidImageUrlException extends DomainException {
    public InvalidImageUrlException() {
        super("Image url cannot be empty or blank");
    }
}