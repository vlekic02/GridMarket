package com.griddynamics.gridmarket.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.GridUser;
import com.griddynamics.gridmarket.token.GridUserAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GridUserForwardFilter implements GlobalFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(GridUserForwardFilter.class);
  private static final String HEADER_KEY = "grid-user";

  private final ObjectMapper objectMapper;

  public GridUserForwardFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .filter(authentication -> authentication instanceof GridUserAuthenticationToken)
        .cast(GridUserAuthenticationToken.class)
        .flatMap(authentication -> injectUserHeader(authentication, exchange, chain))
        .switchIfEmpty(chain.filter(exchange));
  }

  private Mono<Void> injectUserHeader(
      GridUserAuthenticationToken authentication,
      ServerWebExchange exchange,
      GatewayFilterChain chain
  ) {
    GridUser user = authentication.getPrincipal();
    String userHeader;
    try {
      userHeader = objectMapper.writeValueAsString(user);
    } catch (JsonProcessingException e) {
      LOGGER.error("Failed to serialize grid user !", e);
      return chain.filter(exchange);
    }
    ServerHttpRequest request = exchange.getRequest().mutate()
        .header(HEADER_KEY, userHeader)
        .build();
    return chain.filter(exchange.mutate().request(request).build());
  }
}
