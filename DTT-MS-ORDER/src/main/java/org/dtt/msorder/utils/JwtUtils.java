package org.dtt.msorder.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUtils {

    public Jwt getToken() {
        JwtAuthenticationToken auth =
                (JwtAuthenticationToken) SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (auth == null) {
            throw new RuntimeException("No authentication found");
        }

        return auth.getToken();
    }

    public UUID getUserId() {
        String userId = getToken().getClaimAsString("userId");

        if (userId == null) {
            throw new RuntimeException("JWT sin userId");
        }

        return UUID.fromString(userId);
    }
}
