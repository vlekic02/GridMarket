package com.griddynamics.gridmarket.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
class PostgresUserRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  private UserRepository userRepository;

  @Autowired
  private JdbcTemplate template;

  @BeforeEach
  void setup() {
    userRepository = new PostgresUserRepository(template);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(template, "ban", "\"user\"", "role");
  }

  @Test
  void shouldReturnEmptyOptionalIfNoUser() {
    Optional<User> userOptional = userRepository.findById(1);
    assertTrue(userOptional.isEmpty());
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)"
  })
  void shouldReturnUserIfCorrectId() {
    Optional<User> userOptional = userRepository.findById(1);
    User user = userOptional.get();
    assertTrue(user.getId() == 1 && user.getRole().getName().equals("MEMBER"));
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)",
      "insert into ban values (1, 1, 1, '2024-01-08 04:05:06', 'testReason')"
  })
  void shouldReturnCorrectBanDataForUser() {
    User user = userRepository.findById(1).get();
    assertEquals("testReason", user.getBan().getReason());
  }

  @Test
  void shouldReturnEmptyListIfNoUsers() {
    List<User> users = userRepository.findAll();
    assertThat(users).isEmpty();
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (2, 'test', 'test', 'test2', 1, 0)",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 0)",
      "insert into \"user\" values (3, 'test', 'test', 'test3', 1, 0)"
  })
  void shouldReturnAllUsers() {
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(3);
  }
}
