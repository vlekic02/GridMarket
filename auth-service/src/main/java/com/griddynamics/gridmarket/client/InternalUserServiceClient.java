package com.griddynamics.gridmarket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.UserInfo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InternalUserServiceClient {

  private final RestClient userClient;
  private final ObjectMapper objectMapper;

  public InternalUserServiceClient(ObjectMapper objectMapper) {
    this.userClient = RestClient.create("http://user-service:8080/internal");
    this.objectMapper = objectMapper;
  }

  public UserInfo getUserInfo(long id) {
    return userClient
        .get()
        .uri("/users/{id}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(UserInfo.class);
  }

}
