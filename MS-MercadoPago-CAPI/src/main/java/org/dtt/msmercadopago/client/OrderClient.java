package org.dtt.msmercadopago.client;

import org.dtt.msmercadopago.DTO.OrderDetailsResponse;
import org.dtt.msmercadopago.DTO.OrderStatus;
import org.dtt.msmercadopago.config.OrderFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        url = "${api.order}",
        configuration = OrderFeignConfig.class
)
public interface OrderClient {

    @PatchMapping("/orders/{orderId}/users/{userId}")
    OrderDetailsResponse processOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID userId,
            @RequestParam OrderStatus newStatus,
            @RequestParam(required = false) String paymentId
    );
}