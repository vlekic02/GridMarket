package com.griddynamics.gridmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.exceptions.UserExistsException;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import com.griddynamics.gridmarket.requests.ChangePasswordRequest;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import com.griddynamics.gridmarket.services.PubSubService;
import com.griddynamics.gridmarket.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  private AuthenticationController controller;
  private UserService userService;
  private PasswordEncoder encoder;
  @Mock
  private PubSubService pubSubService;

  @BeforeEach
  void setup() {
    encoder = new BCryptPasswordEncoder();
    userService = new UserService(new InMemoryUserRepository(), encoder, pubSubService);
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
  void shouldCorrectlyChangeUserPassword() {
    User user = (User) userService.loadUserByUsername("User");
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
        "password",
        "newPassword"
    );
    controller.changePassword(changePasswordRequest);
    user = (User) userService.loadUserByUsername("User");
    assertTrue(encoder.matches("newPassword", user.getPassword()));
  }

  @Test
  void shouldReturnErrorIfInvalidOldPasswordProvided() {
    User user = (User) userService.loadUserByUsername("User");
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
        "password1",
        "newPassword"
    );
    String response = controller.changePassword(changePasswordRequest);
    assertEquals("redirect:changepassword?error", response);
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
