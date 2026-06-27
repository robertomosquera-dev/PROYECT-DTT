package org.dtt.msorder.clientW;

import org.dtt.msorder.dto.Request.PaymentOrderRequest;
import org.dtt.msorder.dto.Response.PaymentResponse;
import org.dtt.msorder.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebClientMP {
    
    @Value("${api.mp}")
    private String msMp;

    private final WebClient.Builder webClientBuilder;

    @SuppressWarnings("null")
    public ApiResponse<PaymentResponse> paymentTest(PaymentOrderRequest orderRequest) {
        return webClientBuilder
                .baseUrl(msMp)
                .build()
                .post()
                .uri("/payment-test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<PaymentResponse>>() {})
                .block();
    }

}
