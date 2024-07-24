package com.griddynamics.gridmarket.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.impl.PostgresUserRepository;
import com.griddynamics.gridmarket.services.UserService;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJdbcTest
@Sql(value = "/schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private UserService userService;

  private UserController userController;

  @BeforeEach
  void setup() {
    userService = new UserService(new PostgresUserRepository(jdbcTemplate));
    userController = new UserController(userService);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "ban", "\"user\"", "role");
  }

  @Test
  void shouldReturnEmptyDataIfNoUser() {
    assertThat(userController.getAllUsers(PageRequest.of(0, 15)).getData()).isEmpty();
  }

  @Test
  void shouldThrowIfUserDoesntExist() {
    assertThrows(NotFoundException.class, () -> userController.getUserById(1));
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)",
      "insert into ban values (1, 1, 1, '2024-01-08 04:05:06', 'testReason')"
  })
  void shouldReturnUserIfExist() {
    User user = userController.getUserById(1).getData();
    assertTrue(
        user.getId() == 1
            && user.getRole().getName().equals("MEMBER")
            && user.getBan().getReason().equals("testReason")
    );
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (2, 'test', 'test', 'test2', 1, 0)",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)",
      "insert into \"user\" values (3, 'test', 'test', 'test3', 1, 0)"
  })
  void shouldReturnAllUsers() {
    Collection<User> users = userController.getAllUsers(PageRequest.of(0, 30)).getData();
    assertThat(users).hasSize(3);
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)",
      "insert into \"user\" values (2, 'test', 'test', 'test2', 1, 0)",
      "insert into \"user\" values (3, 'test', 'test', 'test3', 1, 0)",
      "insert into \"user\" values (4, 'test', 'test', 'test4', 1, 0)",
      "insert into \"user\" values (5, 'test', 'test', 'test5', 1, 0)"
  })
  void shouldReturnCorrectlyPaginatedResult() {
    Pageable pageable = PageRequest.of(3, 1);
    Collection<User> users = userController.getAllUsers(pageable).getData();
    assertThat(users).hasSize(1).satisfies(usersCollection -> {
      User user = usersCollection.iterator().next();
      assertEquals(4, user.getId());
    });
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 150.25)"
  })
  void shouldReturnCorrectBalanceForUser() {
    Balance balance = userController.getUserBalance(1).getData();
    assertEquals(150.25, balance.getAmount());
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')"
  })
  void shouldReturnCorrectMemberAfterCreating() {
    userService.createMember("TestName", "TestSurname", "TestUsername");
    User user = userService.getUserByUsername("TestUsername");
    assertTrue(
        "TestName".equals(user.getName())
            && "TestSurname".equals(user.getSurname())
            && "TestUsername".equals(user.getUsername())
    );
  }
}
