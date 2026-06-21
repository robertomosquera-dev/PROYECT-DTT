package org.dtt.msmercadopago.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MerchantOrderNotification(
        String id,
        MpOrderStatus status,
        String type
) {
    public enum MpOrderStatus {
        paid,
        expired,
        payment_in_process,
        opened,
        closed
    }
}
