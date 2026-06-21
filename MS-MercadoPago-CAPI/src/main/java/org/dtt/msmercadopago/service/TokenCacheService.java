package org.dtt.msmercadopago.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenCacheService {

    private String cachedToken;
    private Instant expiresAt;

    public boolean isTokenValid() {
        return cachedToken != null && Instant.now().isBefore(expiresAt.minusSeconds(30));
        //                                          margen de 30s antes de expirar 👆
    }

    public String getToken() {
        return cachedToken;
    }

    public void saveToken(String token, Instant expiresAt) {
        this.cachedToken = token;
        this.expiresAt = expiresAt;
    }
}
