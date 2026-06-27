package org.dtt.msmercadopago.controller;


import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.dtt.msmercadopago.DTO.*;
import org.dtt.msmercadopago.client.WebClientOrder;
import org.dtt.msmercadopago.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ControllerTest {

    private final PaymentService paymentService;
    private final WebClientOrder webClientOrder;

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> paymentTest(@RequestBody OrderRequest orderRequest)
            throws MPException, MPApiException {
        PaymentResponse paymentResponse = paymentService.createPreference(orderRequest);
        ApiResponse<PaymentResponse> apiResponse = ApiResponse.success(
                "Payment created successfully",
                200,
                paymentResponse
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/webhook-test")
    public ResponseEntity<Void> getWebhook(@RequestBody String rawBody) {
        log.info("📩 Webhook raw: {}", rawBody);
        return ResponseEntity.ok().build();
    }

//    //Se cancela automaticamente por el timpo vencido
//    // o  el usuario puede cancelar su order
//    //confirma un pedido
//    @PostMapping("/webhook-test")
//    public ResponseEntity<Void> getWebhook(
//            @RequestBody MerchantOrderNotification notification) {
//
//        log.info("📩 Webhook recibido: {}", notification);
//
//        String type = notification.type();
//
//        if (type == null || !type.toLowerCase().contains("merchant_order")) {
//            log.warn("⚠️ Tipo de webhook ignorado: {}", type);
//            return ResponseEntity.ok().build();
//        }
//
//        try {
//
//            WebhookOrderData data = paymentService.processWebhook(
//                    Long.parseLong(notification.id())
//            );
//
//            UUID userId = UUID.fromString(
//                    data.metadata().get("user_id")
//            );
//
//            UUID orderId = UUID.fromString(
//                    data.metadata().get("order_id")
//            );
//
//            BigDecimal totalPaid = data.payments().stream()
//                    .filter(p -> "approved".equalsIgnoreCase(p.status()))
//                    .map(p -> p.totalPaidAmount() != null
//                            ? p.totalPaidAmount()
//                            : BigDecimal.ZERO)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            BigDecimal totalOrder = data.totalAmount();
//
//            boolean isFullyPaid =
//                    totalPaid.compareTo(totalOrder) >= 0;
//
//            /*
//             * 1. Expirada
//             */
//            if ("expired".equalsIgnoreCase(data.status())) {
//
//                webClientOrder.processOrder(
//                        orderId,
//                        userId,
//                        OrderStatus.CANCELLED
//                );
//
//                log.info("⛔ Orden cancelada por expiración: {}", orderId);
//                return ResponseEntity.ok().build();
//            }
//
//            /*
//             * 2. Pagada completamente
//             */
//            if (isFullyPaid) {
//
//                webClientOrder.processOrder(
//                        orderId,
//                        userId,
//                        OrderStatus.COMPLETED
//                );
//
//                log.info(
//                        "✅ Orden completada. orderId={}, totalPaid={}, totalOrder={}",
//                        orderId,
//                        totalPaid,
//                        totalOrder
//                );
//
//                return ResponseEntity.ok().build();
//            }
//
//            /*
//             * 3. Aún pendiente
//             */
//            webClientOrder.processOrder(
//                    orderId,
//                    userId,
//                    OrderStatus.WAITING_PAYMENT
//            );
//
//            log.info(
//                    "⏳ Orden esperando pago. orderId={}, totalPaid={}, totalOrder={}",
//                    orderId,
//                    totalPaid,
//                    totalOrder
//            );
//
//        } catch (Exception e) {
//
//            log.error(
//                    "❌ Error procesando webhook MercadoPago: {}",
//                    e.getMessage(),
//                    e
//            );
//        }
//
//        return ResponseEntity.ok().build();
//    }
}
