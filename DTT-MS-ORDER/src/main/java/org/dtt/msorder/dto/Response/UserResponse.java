package org.dtt.msorder.dto.Response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName
) {

}
