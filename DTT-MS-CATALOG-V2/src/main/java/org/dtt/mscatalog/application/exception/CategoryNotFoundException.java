package org.dtt.mscatalog.application.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Category not found with id: " + id);
    }
    public CategoryNotFoundException(String field, String value) {
        super("Category not found with %s: '%s'".formatted(field, value));
    }
}