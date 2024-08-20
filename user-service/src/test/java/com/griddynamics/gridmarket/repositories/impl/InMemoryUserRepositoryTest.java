package com.griddynamics.gridmarket.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

  private static UserRepository userRepository;

  @BeforeEach
  void setup() {
    userRepository = new InMemoryUserRepository();
  }

  @Test
  void shouldReturnAllUsers() {
    List<User> users = userRepository.findAll();
    assertThat(users).isNotEmpty();
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

  @Test
  void shouldReturnUserIfCorrectUsername() {
    Optional<User> userOptional = userRepository.findByUsername("imirkovic");
    assertEquals(1, userOptional.get().getId());
  }

  @Test
  void shouldReturnCorrectUserAfterCreation() {
    userRepository.createMember("TestName", "TestSurname", "TestUsername");
    User user = userRepository.findByUsername("TestUsername").get();
    assertTrue(
        "TestName".equals(user.getName())
            && "TestSurname".equals(user.getSurname())
            && "TestUsername".equals(user.getUsername())
            && user.getRole().getId() == 1
    );
  }

  @Test
  void shouldCorrectlyDeleteUser() {
    userRepository.deleteUser(1);
    Optional<User> userOptional = userRepository.findById(1);
    assertTrue(userOptional.isEmpty());
  }

  @Test
  void shouldCorrectlyAddBalance() {
    userRepository.addBalance(1, 10);
    User user = userRepository.findById(1).get();
    assertEquals(1510, user.getBalance().getAmount());
  }

  @Test
  void shouldCorrectlySubtractBalance() {
    userRepository.subtractBalance(1, 10);
    User user = userRepository.findById(1).get();
    assertEquals(1490, user.getBalance().getAmount());
  }
}
