package org.dtt.msmercadopago.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientOrderConfig {

    @Value("${api.order}")
    public String urlOrder;

    @Bean("clientOrder")
    public WebClient webClientOrder(){
        return WebClient
                .builder()
                .baseUrl(urlOrder)
                .build();
    }

}
