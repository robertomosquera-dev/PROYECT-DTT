package org.dtt.msauthpublic.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
