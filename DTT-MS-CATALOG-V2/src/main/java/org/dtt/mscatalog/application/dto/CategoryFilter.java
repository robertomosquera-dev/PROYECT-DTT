package org.dtt.mscatalog.application.dto;

import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;

import java.util.UUID;

@Builder
public record CategoryFilter(
        // UUID productId,
        UUID parentId,
        CategoryStatus status
) {}
