package org.dtt.msmercadopago.controller;


import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.dtt.msmercadopago.DTO.*;
import org.dtt.msmercadopago.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
class ControllerPayment {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> payment(@RequestBody OrderRequest orderRequest)
            throws MPException, MPApiException {
        PaymentResponse paymentResponse = paymentService.createPreference(orderRequest);
        return ResponseEntity.ok(ApiResponse.success("Payment created successfully", 200, paymentResponse));
    }

    @PostMapping("/webhook-test")
    public ResponseEntity<Void> getWebhook(@RequestBody WebhookNotification notification) {
        log.info("📩 Webhook: type={}, action={}, status={}",
                notification.type(), notification.action(), notification.status());
        try {
            if (notification.isPaymentCreated()) {
                log.info("💳 Payment id: {}", notification.getPaymentId());
                return ResponseEntity.ok().build();
            }

            if (notification.isMerchantOrder()) {
                if (notification.isClosed()) {
                    paymentService.processOrderFromWebhook(notification.getMerchantOrderId(), OrderStatus.COMPLETED);
                } else if (notification.isExpired()) {
                    paymentService.processOrderFromWebhook(notification.getMerchantOrderId(), OrderStatus.CANCELLED);
                }
            }
        } catch (Exception e) {
            log.error("❌ Error procesando webhook: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }
}