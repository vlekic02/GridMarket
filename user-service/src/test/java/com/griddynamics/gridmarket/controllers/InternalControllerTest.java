package com.griddynamics.gridmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.internal.UserInternalDto;
import com.griddynamics.gridmarket.repositories.impl.PostgresUserRepository;
import com.griddynamics.gridmarket.services.UserService;
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
class InternalControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private InternalController internalController;

  @BeforeEach
  void setup() {
    UserService userService = new UserService(new PostgresUserRepository(jdbcTemplate));
    internalController = new InternalController(userService);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "ban", "\"user\"", "role");
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into \"user\" values (1, 'test', 'test', 'test', 1, 10)",
      "insert into ban values (1, 1, 1, '2024-01-08 04:05:06', 'testReason')"
  })
  void shouldReturnCorrectInternalUserDto() {
    UserInternalDto userDto = internalController.getUserById(1);
    assertTrue(
        userDto.getId() == 1
            && userDto.getBalance() == 10
            && "test".equals(userDto.getName())
            && "test".equals(userDto.getSurname())
            && "test".equals(userDto.getUsername())
            && "MEMBER".equals(userDto.getRole())
    );
  }
}
