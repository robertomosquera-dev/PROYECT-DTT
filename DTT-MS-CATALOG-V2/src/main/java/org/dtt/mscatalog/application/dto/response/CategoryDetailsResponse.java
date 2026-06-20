package org.dtt.mscatalog.application.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryDetailsResponse(
        UUID id,
        String name,
        String path
) {
}
