package org.dtt.mscatalog;

import org.dtt.mscatalog.infrastructure.utils.PropertiesConfigurationS3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan()
public class DttMsCatalogV2Application {

    public static void main(String[] args) {
        SpringApplication.run(DttMsCatalogV2Application.class, args);
    }

}
