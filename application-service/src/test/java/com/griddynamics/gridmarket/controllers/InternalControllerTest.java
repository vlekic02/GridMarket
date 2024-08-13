package com.griddynamics.gridmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Price;
import com.griddynamics.gridmarket.repositories.impl.PostgresApplicationRepository;
import com.griddynamics.gridmarket.services.ApplicationService;
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
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJdbcTest
@Sql(value = "/schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS,
    config = @SqlConfig(separator = "@@"))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InternalControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  private InternalController internalController;
  @Autowired
  private JdbcTemplate template;

  @BeforeEach
  void setup() {
    internalController = new InternalController(
        new ApplicationService(new PostgresApplicationRepository(template), null, null));
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(template, "review", "application", "discount");
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, 'path', 1, 20, null)",
      "insert into sellable_application values (1, default, default)"
  })
  void shouldReturnCorrectPriceForApplication() {
    Price price = internalController.getApplicationPriceById(1).getData();
    assertEquals(20, price.getPrice());
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, 'path', 1, 20, null)"
  })
  void shouldThrowIfApplicationIsNotVerified() {
    assertThrows(NotFoundException.class, () -> internalController.getApplicationPriceById(1));
  }
}
