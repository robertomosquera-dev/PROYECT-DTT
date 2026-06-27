package org.dtt.msmercadopago.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
public class AuthClient {

    @Value("${auth.service-name}")
    private String serviceName;

    @Value("${auth.api-key}")
    private String apiKey;

    @Value("${auth.url}")
    private String authUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private String cachedToken;
    private Instant tokenObtainedAt;
    private static final long TOKEN_TTL_SECONDS = 3500;

    public synchronized String getToken() {
        if (isTokenExpired()) {
            renewToken();
        }
        return cachedToken;
    }

    private boolean isTokenExpired() {
        return cachedToken == null
                || tokenObtainedAt == null
                || Instant.now().isAfter(tokenObtainedAt.plusSeconds(TOKEN_TTL_SECONDS));
    }

    private void renewToken() {
        log.info("Obteniendo token para: {}", serviceName);

        Map response = restTemplate.postForObject(
                authUrl + "/service/token",
                new HttpEntity<>(null, headers()),
                Map.class
        );

        cachedToken = (String) response.get("token");
        tokenObtainedAt = Instant.now();

        log.info("Token obtenido para: {}", serviceName);
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-SERVICE-NAME", serviceName);
        headers.set("X-API-KEY", apiKey);
        return headers;
    }
}