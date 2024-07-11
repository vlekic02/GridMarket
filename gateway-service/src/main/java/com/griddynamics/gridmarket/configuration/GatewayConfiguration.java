package com.griddynamics.gridmarket.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder routeBuilder) {
    return routeBuilder.routes()
        .route("application-service", route -> route
            .path("/v1/applications/**")
            .uri("http://application-service:8080")
        )
        .route("user-service", route -> route
            .path("/v1/users/**")
            .uri("http://user-service:8080")
        )
        .route("order-service", route -> route
            .path("/v1/orders/**")
            .uri("http://order-service:8080")
        )
        .build();
  }
}
