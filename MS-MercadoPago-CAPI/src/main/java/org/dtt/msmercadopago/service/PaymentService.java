package org.dtt.msmercadopago.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.merchantorder.MerchantOrder;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dtt.msmercadopago.DTO.*;
import org.dtt.msmercadopago.client.OrderClient;
import org.dtt.msmercadopago.config.MercadoPagoCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    /**
     * Crear Exepciones personalizadas para los errores
     */

    //Credentials
    private final MercadoPagoCredentials credentials;

    //Profiles
    private final Environment env;

    //Beans
    private final PreferenceBackUrlsRequest backUrlsRequest;
    private final PreferenceClient client;


    @Value("${url.notification}")
    private String UrlNotification;

    private final MerchantOrderClient merchantOrderClient;

    private final OrderClient orderClient;


    @PostConstruct
    public void initMercadoPago() {
        MercadoPagoConfig.setAccessToken(credentials.accessToken());
        log.info("MercadoPago initialized");
        log.info("BackUrls - success={}, failure={}, pending={}",
                backUrlsRequest.getSuccess(),
                backUrlsRequest.getFailure(),
                backUrlsRequest.getPending()
            );
    }

    public PaymentResponse createPreference(OrderRequest orderRequest)
            throws MPException, MPApiException {

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        log.info("Creating preference for orderId={}", orderRequest.orderId());

        String profile = env.getProperty("spring.profiles.active", "default");

        List<PreferenceItemRequest> items = createItems(orderRequest.items());
        PreferencePayerRequest payer = createPayer(orderRequest.payer());

        PreferenceRequest preferenceRequest = PreferenceRequest
                .builder()
                .items(items)
                .backUrls(                                          
                    PreferenceBackUrlsRequest.builder()
                        .success(backUrlsRequest.getSuccess())
                    .failure(backUrlsRequest.getFailure())
                        .pending(backUrlsRequest.getPending())
                        .build()
                )
                .payer(payer)
                .expirationDateFrom(now)
                .expirationDateTo(now.plusDays(3))
                //.autoReturn("approved")                            // Descomentar al probar en produccion XD
                .externalReference(orderRequest.orderId().toString())
                .notificationUrl(UrlNotification)
                .metadata(Map.of(
                        "order_id", orderRequest.orderId().toString(),
                        "user_id", orderRequest.payer().payerId().toString(),
                        "platform", orderRequest.platform(),
                        "env", profile,
                        "created_at", Instant.now().toString()
                ))
                .build();

        try {
            Preference preference = client.create(preferenceRequest);

            log.info("Preference created: mpPreferenceId={}, initPoint={}",
                    preference.getId(),
                    preference.getInitPoint());

            return new PaymentResponse(
                    preference.getId(),
                    preference.getInitPoint(),
                    orderRequest.orderId().toString(),
                    PaymentStatus.PENDING
            );

        } catch (MPApiException e) {
            System.out.println("=== ERROR MERCADOPAGO ===");
            System.out.println("Status: " + e.getStatusCode());

            if (e.getApiResponse() != null) {
                System.out.println("Response: " + e.getApiResponse().getContent());
            }

            throw e;
        } catch (MPException e) {
            log.error("MP Exception: {}", e.getMessage());
            throw e;
        }

    }
    public void processOrderFromWebhook(String merchantOrderId, OrderStatus status)
            throws MPException, MPApiException {
        MerchantOrder order = merchantOrderClient.get(Long.parseLong(merchantOrderId));
        Preference preference = client.get(order.getPreferenceId());

        UUID orderId = UUID.fromString(order.getExternalReference());
        UUID userId = UUID.fromString(preference.getMetadata().get("user_id").toString());

        String paymentId = order.getPayments().isEmpty()
                ? null
                : order.getPayments().get(0).getId().toString();

        try {
            orderClient.processOrder(orderId, userId, status, paymentId);
            log.info("📦 orderId={}, userId={}, status={}, paymentId={}", orderId, userId, status, paymentId);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Invalid transition")) {
                log.warn("⚠️ Orden {} ya está en estado {}, ignorando webhook duplicado", orderId, status);
            } else {
                throw e;
            }
        }
    }
    /**
     * Es un mapeo de una lista con validacion nada mas.
     * @comment Usar la dependecia validation para verificar la lista de items, queda mas limpio el metodo
     * @param List<ItemRequest>
     * @return List<PreferenceItemRequest>
     */
    private List<PreferenceItemRequest> createItems(List<ItemRequest> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be null or empty");
        }

        return items.stream()
                .map(item -> {

                    if (item.quantity() <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than 0");
                    }

                    if (item.unitPrice() == null) {
                        throw new IllegalArgumentException("Unit price cannot be null");
                    }

                    return PreferenceItemRequest.builder()
                            .id(item.productId().toString())
                            .title(item.title())
                            .description(item.description())
                            .categoryId(item.category())
                            .quantity(item.quantity())
                            .unitPrice(item.unitPrice())
                            .currencyId(item.currency())
                            .pictureUrl(item.pictureUrl())
                            .build();
                })
                .toList();
    }

    /**
     * Solo es un mapeo
     * @param payerRequest
     * @return PreferencePayerRequest
     */

    private PreferencePayerRequest createPayer(PayerRequest payerRequest) {

        if (payerRequest == null) {
            throw new IllegalArgumentException("Payer cannot be null");
        }

        return PreferencePayerRequest.builder()
                .email(payerRequest.email())
                .name(payerRequest.name())
                .surname(payerRequest.surname())
                .build();
    }
}