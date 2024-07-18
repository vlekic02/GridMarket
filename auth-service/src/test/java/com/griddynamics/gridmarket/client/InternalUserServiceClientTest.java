package com.griddynamics.gridmarket.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.internal.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest({InternalUserServiceClient.class})
class InternalUserServiceClientTest {

  @Autowired
  private InternalUserServiceClient client;

  @Autowired
  private MockRestServiceServer server;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldGetCorrectUserInfo() throws JsonProcessingException {
    UserInfo expectedUserInfo = new UserInfo(1, "Test", "Test", "Test", "Admin", 100);
    String response = objectMapper.writeValueAsString(expectedUserInfo);
    server.expect(requestTo("http://user-service:8080/internal/users/1"))
        .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    UserInfo actualUserInfo = client.getUserInfo(1);
    assertEquals(expectedUserInfo, actualUserInfo);
  }
}
