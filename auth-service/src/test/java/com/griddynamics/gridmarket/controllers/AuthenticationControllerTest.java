package com.griddynamics.gridmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.griddynamics.gridmarket.exceptions.UserExistsException;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import com.griddynamics.gridmarket.services.PubSubService;
import com.griddynamics.gridmarket.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  private AuthenticationController controller;
  private UserService userService;
  @Mock
  private PubSubService pubSubService;

  @BeforeEach
  void setup() {
    userService = new UserService(new InMemoryUserRepository(), pubSubService);
    controller = new AuthenticationController(userService);
  }

  @Test
  void shouldReturnCorrectViewForLoginPath() {
    assertEquals("login-page", controller.loginPage());
  }

  @Test
  void shouldReturnCorrectViewForRegisterPath() {
    assertEquals("register-page", controller.registerPage());
  }

  @Test
  void shouldCorrectlyRegisterUser() {
    UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
        "Test",
        "Test",
        "Test", "Test"
    );
    controller.registerUser(userRegistrationRequest);
    assertDoesNotThrow(() -> userService.loadUserByUsername("Test"));
  }

  @Test
  void shouldThrowIfUserAlreadyExist() {
    UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
        "Test",
        "Test",
        "User", "Test"
    );
    assertThrows(UserExistsException.class, () -> controller.registerUser(userRegistrationRequest));
  }
}
