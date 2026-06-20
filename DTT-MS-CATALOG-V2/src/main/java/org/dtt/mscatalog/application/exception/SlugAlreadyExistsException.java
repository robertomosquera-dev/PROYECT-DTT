package org.dtt.mscatalog.application.exception;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String slug) {
        super("Slug '%s' is already in use".formatted(slug));
    }
}