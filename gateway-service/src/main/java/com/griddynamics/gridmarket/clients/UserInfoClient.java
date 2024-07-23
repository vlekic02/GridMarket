package com.griddynamics.gridmarket.clients;

import com.griddynamics.gridmarket.models.GridUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserInfoClient {

  private final WebClient userInfoClient;

  public UserInfoClient(
      WebClient.Builder builder,
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri
  ) {
    if (issuerUri.endsWith("/")) {
      issuerUri = issuerUri.substring(0, issuerUri.length() - 1);
    }
    this.userInfoClient = builder
        .baseUrl(issuerUri)
        .build();
  }

  public Mono<GridUser> getUserInfo(Jwt jwt) {
    return userInfoClient
        .get()
        .uri("/userinfo")
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
        .retrieve()
        .bodyToMono(GridUser.class);
  }
}
