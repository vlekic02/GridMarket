package com.griddynamics.gridmarket.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

  private static UserRepository userRepository;

  @BeforeAll
  static void setup() {
    userRepository = new InMemoryUserRepository();
  }

  @Test
  void shouldReturnAllUsers() {
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(4);
  }

  @Test
  void shouldReturnUserIfCorrectIdSupplied() {
    Optional<User> userOptional = userRepository.findById(1);
    assertEquals(1, userOptional.get().getId());
  }

  @Test
  void shouldReturnEmptyOptionalIfIncorrectIdSupplied() {
    Optional<User> userOptional = userRepository.findById(50);
    assertTrue(userOptional.isEmpty());
  }
}
