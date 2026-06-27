package org.dtt.msmercadopago.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.dtt.msmercadopago.client.AuthClient;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class OrderFeignConfig {

    private final AuthClient authClient;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = authClient.getToken();
            template.header("Authorization", "Bearer " + token);
        };
    }
}