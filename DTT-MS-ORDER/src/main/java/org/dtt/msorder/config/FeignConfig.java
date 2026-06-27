package org.dtt.msorder.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.dtt.msorder.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;

@RequiredArgsConstructor
public class FeignConfig {

    private final JwtUtils jwtUtils;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (template -> {
            try {
                Jwt jwt = jwtUtils.getToken();
                template.header("Authorization", "Bearer " + jwt.getTokenValue());
            } catch (Exception e) {
                // No se pudo obtener el token JWT
            }
        });
    }
}