package org.dtt.msorder.client;

import jakarta.validation.Valid;
import org.dtt.msorder.config.FeignConfig;
import org.dtt.msorder.dto.Request.ReservationRequest;
import org.dtt.msorder.dto.Response.ItemOrderResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.utils.ConsumerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "catalog-service",
        url = "${api.catalog}",
        configuration = FeignConfig.class
)
public interface CatalogClient {
    @PostMapping("/reservation")
    ConsumerResponse<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request
    );

    @PatchMapping("/reservation/{reservationId}/process")
    ConsumerResponse<Void> processReservation(
            @PathVariable UUID reservationId,
            @RequestParam Boolean isConfirmed
    );

    @GetMapping("/reservation/items/{orderId}")
    ConsumerResponse<List<ItemOrderResponse>> getProductByOrderId(
            @PathVariable UUID orderId
    );
}
