package org.dtt.msauthpublic.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterResponse(
        UUID id,
        String name,
        String surname,
        String phone,
        String address,
        String rol,
        String permissions
) {
}
