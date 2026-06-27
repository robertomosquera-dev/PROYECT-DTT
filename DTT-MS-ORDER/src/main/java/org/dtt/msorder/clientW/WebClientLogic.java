package org.dtt.msorder.clientW;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.dto.Request.ReservationRequest;
import org.dtt.msorder.dto.Response.ItemOrderResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile({"default","docker","dev"})
public class WebClientLogic {

    @Value("${api.logic}")
    private String msLogic;

    private final WebClient.Builder webClientBuilder;

    private WebClient client() {
        return webClientBuilder
                .baseUrl(msLogic)
                .build();
    }

    public ReservationResponse reservationProducts(
            ReservationRequest reservation,
            String token
    ) {
        return client()
                .post()
                .uri("/reservation")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(reservation)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<ReservationResponse>>() {})
                .map(ApiResponse::data)
                .block();
    }

    public void processReservation(
            UUID reservationId,
            boolean isConfirmed,
            String token
    ) {
        client()
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/reservation/{reservationId}/process")
                        .queryParam("isConfirmed", isConfirmed)
                        .build(reservationId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<ItemOrderResponse> getItemsFromReservation(
            Map<UUID, List<UUID>> uuidListMap,
            String token
    ) {
        return client()
                .post()
                .uri("/reservation/items")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(uuidListMap)
                .retrieve()
                .bodyToFlux(ItemOrderResponse.class)
                .collectList()
                .block();
    }
}