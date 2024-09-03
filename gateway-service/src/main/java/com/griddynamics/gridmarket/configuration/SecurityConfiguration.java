package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.clients.UserInfoClient;
import com.griddynamics.gridmarket.converter.UserInfoAuthenticationConverter;
import com.griddynamics.gridmarket.handlers.GatewayAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  private final UserInfoClient userInfoClient;
  private final ObjectMapper objectMapper;

  public SecurityConfiguration(UserInfoClient userInfoClient, ObjectMapper objectMapper) {
    this.userInfoClient = userInfoClient;
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
    http.authorizeExchange(authorize -> authorize
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(
                jwt -> jwt.jwtAuthenticationConverter(
                    new UserInfoAuthenticationConverter(userInfoClient)))
            .authenticationEntryPoint(new GatewayAuthenticationEntryPoint(objectMapper))
        );
    return http.build();
  }
}
