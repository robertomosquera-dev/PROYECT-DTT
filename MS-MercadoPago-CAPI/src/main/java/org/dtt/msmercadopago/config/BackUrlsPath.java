package org.dtt.msmercadopago.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "back-urls")
public record BackUrlsPath(
        String success,
        String failure,
        String pending
) {
}
