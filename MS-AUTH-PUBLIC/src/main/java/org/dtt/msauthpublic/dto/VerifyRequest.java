package org.dtt.msauthpublic.dto;

public record VerifyRequest(
        String code,
        String email
) {
}
