package com.griddynamics.gridmarket.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

  private static final String APPLICATION_URL = "http://application-service:8080";
  private static final String USER_URL = "http://user-service:8080";
  private static final String ORDER_URL = "http://order-service:8080";

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder routeBuilder) {
    return routeBuilder.routes()
        .route("application-service", route -> route
            .path("/v1/applications/**")
            .uri(APPLICATION_URL)
        )
        .route("user-service", route -> route
            .path("/v1/users/**")
            .uri(USER_URL)
        )
        .route("order-service", route -> route
            .path("/v1/orders/**")
            .uri(ORDER_URL)
        )
        .route("application-docs", route -> route
            .path("/application/swagger-ui/**", "/application/v3/api-docs/**")
            .filters(f -> f.rewritePath(
                "/application/swagger-ui/(?<remaining>.*)",
                "/swagger-ui/${remaining}")
            )
            .uri(APPLICATION_URL)
        )
        .route("user-docs", route -> route
            .path("/user/swagger-ui/**", "/user/v3/api-docs/**")
            .filters(f -> f.rewritePath(
                "/user/swagger-ui/(?<remaining>.*)",
                "/swagger-ui/${remaining}")
            )
            .uri(USER_URL)
        )
        .route("order-docs", route -> route
            .path("/order/swagger-ui/**")
            .filters(f -> f.rewritePath(
                "/order/swagger-ui/(?<remaining>.*)",
                "/swagger-ui/${remaining}")
            )
            .uri(ORDER_URL)
        )
        .build();
  }
}
