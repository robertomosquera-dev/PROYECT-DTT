package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProductImgResponse(
    UUID id,
    String url,
    Short order
) {}
