package com.griddynamics.gridmarket.client;

import com.griddynamics.gridmarket.models.internal.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InternalUserServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(InternalUserServiceClient.class);

  private final RestClient userClient;

  public InternalUserServiceClient(RestClient.Builder builder) {
    this.userClient = builder
        .baseUrl("http://user-service:8080/internal")
        .build();
  }

  public UserInfo getUserInfo(String username) {
    LOGGER.debug("Calling user service with url /internal/users/{}", username);
    return userClient
        .get()
        .uri("/users/{username}", username)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(UserInfo.class);
  }

}
