package org.dtt.mscatalog.domain.exception;

public class InvalidSlugException extends DomainException {
    public InvalidSlugException(String slug) {
        super("Invalid slug: '%s'".formatted(slug));
    }
}