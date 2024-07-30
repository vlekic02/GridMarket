package com.griddynamics.gridmarket.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.griddynamics.gridmarket.models.Price;
import com.griddynamics.gridmarket.repositories.impl.PostgresApplicationRepository;
import com.griddynamics.gridmarket.services.ApplicationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.jdbc.JdbcTestUtils;

@DataJdbcTest
@Sql(value = "/test-schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("test")
class InternalControllerTest {

  private InternalController internalController;
  @Autowired
  private JdbcTemplate template;

  @BeforeEach
  void setup() {
    internalController = new InternalController(
        new ApplicationService(new PostgresApplicationRepository(template), null));
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(template, "review", "application", "discount");
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, 'path', 1, 20, null)",
  })
  void shouldReturnCorrectPriceForApplication() {
    Price price = internalController.getApplicationPriceById(1).getData();
    assertEquals(20, price.getPrice());
  }
}
