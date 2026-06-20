package org.dtt.msauthpublic.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RolResponse(
        UUID id,
        String name,
        String permissions
) {
}
