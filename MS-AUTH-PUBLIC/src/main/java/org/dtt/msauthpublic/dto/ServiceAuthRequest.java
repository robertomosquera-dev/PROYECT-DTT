package org.dtt.msauthpublic.dto;

public record ServiceAuthRequest(
        String name,
        String apiKey
) {}