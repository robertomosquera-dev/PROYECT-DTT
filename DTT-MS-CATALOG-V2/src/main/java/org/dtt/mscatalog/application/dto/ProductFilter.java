package org.dtt.mscatalog.application.dto;

import lombok.Builder;
import org.dtt.mscatalog.application.dto.request.StatusRequest;

import java.util.List;
import java.util.UUID;

@Builder
public record ProductFilter(
        StatusRequest status,
        List<UUID> categoryIds,
        List<String> categorySlugs
) {}
