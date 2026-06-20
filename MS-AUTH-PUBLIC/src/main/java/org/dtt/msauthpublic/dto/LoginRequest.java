package org.dtt.msauthpublic.dto;

public record LoginRequest(
        String username,
        String password
) {
}
