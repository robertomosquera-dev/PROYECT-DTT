package org.dtt.mscatalog.application.exception;

import java.util.UUID;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(UUID id) {
        super("Image not found with id: " + id);
    }
}