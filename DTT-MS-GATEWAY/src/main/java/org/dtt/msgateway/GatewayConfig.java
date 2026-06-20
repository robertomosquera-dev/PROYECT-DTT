package org.dtt.msgateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${api.auth}")
    private String msAuth;

    @Value("${api.order}")
    private String msOrder;

    @Value("${api.catalog}")
    private String msCatalog;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()


                .route("swagger-auth-base", r -> r
                        .path("/auth/v3/api-docs")
                        .filters(f -> f.rewritePath(
                                "/auth/v3/api-docs",
                                "/v3/api-docs"
                        ))
                        .uri(msAuth)
                )

                .route("swagger-order-base", r -> r
                        .path("/order/v3/api-docs")
                        .filters(f -> f.rewritePath(
                                "/order/v3/api-docs",
                                "/v3/api-docs"
                        ))
                        .uri(msOrder)
                )

                .route("swagger-catalog-base", r -> r
                        .path("/catalog/v3/api-docs")
                        .filters(f -> f.rewritePath(
                                "/catalog/v3/api-docs",
                                "/v3/api-docs"
                        ))
                        .uri(msCatalog)
                )


                .route("swagger-auth", r -> r
                        .path("/auth/v3/api-docs/**")
                        .filters(f -> f.rewritePath(
                                "/auth/v3/api-docs/(?<segment>.*)",
                                "/v3/api-docs/${segment}"
                        ))
                        .uri(msAuth)
                )

                .route("swagger-order", r -> r
                        .path("/order/v3/api-docs/**")
                        .filters(f -> f.rewritePath(
                                "/order/v3/api-docs/(?<segment>.*)",
                                "/v3/api-docs/${segment}"
                        ))
                        .uri(msOrder)
                )

                .route("swagger-catalog", r -> r
                        .path("/catalog/v3/api-docs/**")
                        .filters(f -> f.rewritePath(
                                "/catalog/v3/api-docs/(?<segment>.*)",
                                "/v3/api-docs/${segment}"
                        ))
                        .uri(msCatalog)
                )


                .route("ms-auth", r -> r
                        .path(
                                "/auth/**",
                                "/users/**",
                                "/rol/**",
                                "/service/**"
                        )
                        .uri(msAuth)
                )


                .route("ms-order", r -> r
                        .path("/orders/**")
                        .uri(msOrder)
                )

                .route("ms-catalog", r -> r
                        .path(
                                "/products",
                                "/products/**",

                                "/categories",
                                "/categories/**",

                                "/catalog",
                                "/catalog/**",

                                "/base-products",
                                "/base-products/**",

                                "/reservations",
                                "/reservations/**"
                        )
                        .uri(msCatalog)
                )

                .build();
    }
}