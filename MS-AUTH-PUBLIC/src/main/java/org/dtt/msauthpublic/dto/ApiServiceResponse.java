package org.dtt.msauthpublic.dto;


public record ApiServiceResponse(
        String name,
        String api_key_hash,
        boolean enabled
) {
}
