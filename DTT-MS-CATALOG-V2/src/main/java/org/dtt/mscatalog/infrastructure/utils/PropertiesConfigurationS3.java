package org.dtt.mscatalog.infrastructure.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("docker")
@ConfigurationProperties(prefix = "aws")
public record PropertiesConfigurationS3(
        String accessKey,
        String secretKey,
        String region,
        String bucket
) {
}
