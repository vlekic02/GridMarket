package com.griddynamics.gridmarket.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserServiceTest {

  private static UserService userService;

  @BeforeAll
  static void setup() {
    userService = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder(), null);
  }

  @Test
  void shouldReturnCorrectUserForExistingUsername() {
    User userDetails = (User) userService.loadUserByUsername("User");
    assertTrue(userDetails.getId() == 1
        && "User".equals(userDetails.getUsername())
        && "$2a$12$HxWrdRqiBamt3NGyp7xoreXu2Ig7yVUbtySR1mfgrZSdYBQjOHniG".equals(
        userDetails.getPassword())
    );
  }

  @Test
  void shouldThrowIfUsernameDoesntExist() {
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("Test"));
  }
}
