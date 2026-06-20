package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record CategoryResponse(
    UUID id,
    String name,
    String slug,
    CategoryStatus categoryStatus,
    UUID parentId,
    String imgUrl,
    String path
) implements Serializable {}
