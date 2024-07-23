package com.griddynamics.gridmarket.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class GatewayAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      GatewayAuthenticationEntryPoint.class);

  private final ObjectMapper objectMapper;

  public GatewayAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
    ErrorResource errorResource = ErrorResource.of(
        "Unauthorized",
        HttpStatus.UNAUTHORIZED.value(),
        exception.getMessage()
    );
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    try {
      String responseMessage = objectMapper.writeValueAsString(errorResource);
      byte[] responseBytes = responseMessage.getBytes(StandardCharsets.UTF_8);
      DataBuffer dataBuffer = response.bufferFactory().wrap(responseBytes);
      response.getHeaders().add("Content-Type", "application/vnd.api+json");
      return response.writeWith(Mono.just(dataBuffer));
    } catch (JsonProcessingException e) {
      LOGGER.error("Failed to serialize error object !", e);
    }
    return null;
  }
}
