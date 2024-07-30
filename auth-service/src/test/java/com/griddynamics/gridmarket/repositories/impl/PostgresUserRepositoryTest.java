package com.griddynamics.gridmarket.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
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

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    userRepository = new PostgresUserRepository(jdbcTemplate);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "grid_user");
  }

  @Test
  @Sql(statements = {
      "INSERT INTO grid_user VALUES (1,'testUsername', 'testPassword')"
  })
  void shouldReturnCorrectUserIfValidUsername() {
    Optional<User> userOptional = userRepository.findByUsername("testUsername");
    User user = userOptional.get();
    assertTrue(
        user.getId() == 1
            && "testUsername".equals(user.getUsername())
            && "testPassword".equals(user.getPassword())
    );
  }

  @Test
  @Sql(statements = {
      "INSERT INTO grid_user VALUES (1,'testUsername', 'testPassword')"
  })
  void shouldReturnEmptyOptionalIfInvalidUsername() {
    Optional<User> userOptional = userRepository.findByUsername("NonExistentUsername");
    assertTrue(userOptional.isEmpty());
  }

  @Test
  void shouldCorrectlyRegisterUser() {
    userRepository.addRegisteredUser("TestUsername", "TestPassword");
    User user = userRepository.findByUsername("TestUsername").get();
    assertEquals("TestPassword", user.getPassword());
  }

  @Test
  @Sql(statements = {
      "INSERT INTO grid_user VALUES (1,'testUsername', 'testPassword')"
  })
  void shouldCorrectlyDeleteUserByUsername() {
    userRepository.deleteByUsername("testUsername");
    Optional<User> userOptional = userRepository.findByUsername("testUsername");
    assertTrue(userOptional.isEmpty());
  }

  @Test
  @Sql(statements = {
      "INSERT INTO grid_user VALUES (1,'testUsername', 'testPassword')"
  })
  void shouldCorrectlyChangeUserPassword() {
    userRepository.changePassword("testUsername", "newPassword");
    User user = userRepository.findByUsername("testUsername").get();
    assertEquals("newPassword", user.getPassword());
  }
}
