package com.griddynamics.gridmarket.configuration;

import com.griddynamics.gridmarket.clients.UserInfoClient;
import com.griddynamics.gridmarket.converter.UserInfoAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  private final UserInfoClient userInfoClient;

  public SecurityConfiguration(UserInfoClient userInfoClient) {
    this.userInfoClient = userInfoClient;
  }

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
    http.authorizeExchange(authorize -> authorize
        .anyExchange().authenticated()
    ).oauth2ResourceServer((oauth2) -> oauth2.jwt(
        jwt -> jwt.jwtAuthenticationConverter(
            new UserInfoAuthenticationConverter(userInfoClient))));
    return http.build();
  }
}
