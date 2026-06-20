package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ProductResponse(
    UUID id,
    String name,
    String description,
    ProductBaseStatus status,

    List<ProductImgResponse> images,

    List<CategoryDetailsResponse> categories
) implements Serializable {}
