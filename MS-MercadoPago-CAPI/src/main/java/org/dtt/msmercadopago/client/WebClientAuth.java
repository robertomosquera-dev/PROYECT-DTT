package org.dtt.msmercadopago.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class WebClientAuth {

    @Qualifier("clientAuth")
    private final WebClient webClient;

    public WebClientAuth(@Qualifier("clientAuth") WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${auth.service-name}")
    private String serviceName;

    @Value("${auth.api-key}")
    private String apiKey;

    private final AtomicReference<String> cachedToken = new AtomicReference<>();
    private volatile Instant tokenObtainedAt;
    private static final long TOKEN_TTL_SECONDS = 3500;

    public String getToken() {
        if (isTokenExpired()) {
            synchronized (this) {
                if (isTokenExpired()) {
                    renewToken();
                }
            }
        }
        return cachedToken.get();
    }

    private boolean isTokenExpired() {
        return cachedToken.get() == null
                || tokenObtainedAt == null
                || Instant.now().isAfter(tokenObtainedAt.plusSeconds(TOKEN_TTL_SECONDS));
    }

    private void renewToken() {
        log.info("Obteniendo token para el servicio: {}", serviceName);

        var response = webClient.post()
                .uri("/service/token")
                .header("X-SERVICE-NAME", serviceName)
                .header("X-API-KEY", apiKey)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        cachedToken.set((String) response.get("token"));
        tokenObtainedAt = Instant.now();

        log.info("Token obtenido correctamente para: {}", serviceName);
    }

}
