package org.dtt.msorder.client;

import org.dtt.msorder.dto.Request.PaymentOrderRequest;
import org.dtt.msorder.dto.Response.PaymentResponse;
import org.dtt.msorder.utils.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "mp-service",
        url = "${api.mp}"
)
public interface MercadoPagoClient {

    @PostMapping("/payment")
    ApiResponse<PaymentResponse> createPayment(
            @RequestBody PaymentOrderRequest request
    );

}
