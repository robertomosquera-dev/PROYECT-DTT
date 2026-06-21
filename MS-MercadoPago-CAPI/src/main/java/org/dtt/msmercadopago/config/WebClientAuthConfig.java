package org.dtt.msmercadopago.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientAuthConfig {

    @Value("${auth.url}")
    private String urlAuth;

    @Bean("clientAuth")
    public WebClient webClientOrder(){
        return WebClient
                .builder()
                .baseUrl(urlAuth)
                .build();
    }

}
