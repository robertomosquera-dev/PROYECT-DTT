package org.dtt.msmercadopago.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookNotification(
        String action,
        String id,
        String status,
        String type,
        @JsonProperty("user_id") Long userId,
        Integer version,
        WebhookData data
) {
    public record WebhookData(String id) {}

    public boolean isMerchantOrder() {
        return "topic_merchant_order_wh".equals(type);
    }

    public boolean isPaymentCreated() {
        return "payment".equals(type) && "payment.created".equals(action);
    }

    public boolean isClosed() {
        return "closed".equals(status);
    }

    public boolean isExpired() {
        return "expired".equals(status);
    }

    public String getPaymentId() {
        return data != null ? data.id() : null;
    }

    public String getMerchantOrderId() {
        return id;
    }
}