package com.griddynamics.gridmarket.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserServiceTest {

  private static UserService userService;

  @BeforeAll
  static void setup() {
    userService = new UserService(new InMemoryUserRepository());
  }

  @Test
  void shouldThrowIfIncorrectIdSupplied() {
    assertThrows(NotFoundException.class, () -> userService.getUserById(10));
  }

  @Test
  void shouldReturnUserIfCorrectIdSupplied() {
    User user = userService.getUserById(1);
    assertEquals(1, user.getId());
  }

  @Test
  void shouldReturnAllUsers() {
    Collection<User> users = userService.getAllUsers();
    assertThat(users).hasSize(4);
  }

  @Test
  void shouldReturnCorrectBalanceForUser() {
    Balance balance = userService.getUserBalance(1);
    assertEquals(1500, balance.getAmount());
  }
}
