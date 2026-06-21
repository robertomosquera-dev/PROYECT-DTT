package org.dtt.msmercadopago.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mp-config")
public record MercadoPagoCredentials(
    String urlMercadoPago,
    String accessToken,
    String publicKey
) {
}
