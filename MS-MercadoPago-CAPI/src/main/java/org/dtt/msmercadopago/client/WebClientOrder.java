package org.dtt.msmercadopago.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dtt.msmercadopago.DTO.OrderDetailsResponse;
import org.dtt.msmercadopago.DTO.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Component
public class WebClientOrder {

    private WebClient webClient;
    private WebClientAuth clientAuth;

    public WebClientOrder(
            @Qualifier("clientOrder") WebClient webClient,
                          WebClientAuth clientAuth) {
        this.webClient = webClient;
        this.clientAuth = clientAuth;
    }

    public void processOrder(
            UUID orderId,
            UUID userId,
            OrderStatus orderStatus
    ) {

        String token = clientAuth.getToken();

        OrderDetailsResponse response = webClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/{orderId}/users/{userId}")
                        .queryParam("newStatus", orderStatus)
                        .build(orderId, userId)
                )
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(OrderDetailsResponse.class)
                .block();

        log.info(
                "processOrder response - orderId={}, userId={}, status={}, response={}",
                orderId,
                userId,
                orderStatus,
                response
        );
    }
}
