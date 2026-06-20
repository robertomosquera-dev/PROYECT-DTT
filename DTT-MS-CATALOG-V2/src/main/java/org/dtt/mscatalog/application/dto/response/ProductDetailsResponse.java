package org.dtt.mscatalog.application.dto.response;

import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;

import java.io.Serializable;
import java.util.UUID;

public record ProductDetailsResponse(
        UUID id,
        String name,
        ProductBaseStatus status,
        ProductImgResponse image
) implements Serializable {
}
