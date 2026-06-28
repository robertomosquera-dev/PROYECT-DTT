package org.dtt.msorder.client;

import org.dtt.msorder.dto.Request.OrderNotificationRequest;
import org.dtt.msorder.dto.Request.OrderPaymentConfirmationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "n8n-service",
        url = "${api.n8n}"
)
public interface N8nClient {

    @PostMapping("/webhook/order-creation-notification")
    void sendCreatedOrderNotification(OrderNotificationRequest orderNotificationRequest);

    @PostMapping("/webhook/order-payment-confirmation")
    void sendPaymentConfirmationNotification(OrderPaymentConfirmationRequest request);

}
