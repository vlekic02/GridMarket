package com.griddynamics.gridmarket.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
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
class PostgresApplicationRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  private ApplicationRepository applicationRepository;

  @Autowired
  private JdbcTemplate template;

  @BeforeEach
  void setup() {
    applicationRepository = new PostgresApplicationRepository(template);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(template, "review", "application", "discount");
  }

  @Test
  @Sql(statements = {
      "insert into application values (default, 'Test', null, 'path', 1, 20, null)",
      "insert into application values (default, 'Test2', null, 'path', 3, 25, null)"
  })
  void shouldReturnAllApplications() {
    List<Application> applications = applicationRepository.findAll();
    assertThat(applications).hasSize(2);
  }

  @Test
  @Sql(statements = "insert into application values (1, 'Test', null, 'path', 1, 20, null)")
  void shouldReturnApplicationIfIdPresent() {
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertFalse(applicationOptional.isEmpty());
  }

  @Test
  void shouldReturnEmptyOptionalIfIdIsNotPresent() {
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertTrue(applicationOptional.isEmpty());
  }

  @Test
  @Sql(statements = "insert into application values (1, 'Test', null, 'path', 1, 20, null)")
  void shouldReturnNoReviewsIfApplicationDontHaveAny() {
    Application application = applicationRepository.findById(1).get();
    List<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertTrue(reviews.isEmpty());
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, 'path', 1, 20, null)",
      "insert into review values (default, 1, 'msg', 5, 1)",
      "insert into review values (default, 2, 'msg', 5, 1)"
  })
  void shouldReturnAllReviewForApplication() {
    Application application = applicationRepository.findById(1).get();
    List<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertThat(reviews).hasSize(2);
  }

  @Test
  @Sql(statements = {
      "insert into discount values (1, 'test discount', 'PERCENTAGE', 20, null, null)",
      "insert into application values (1, 'Test', null, 'path', 1, 20, 1)"
  })
  void shouldCorrectlyReturnApplicationWithDiscount() {
    Application application = applicationRepository.findById(1).get();
    assertNotNull(application.getDiscount());
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, 'path', 1, 20, default)"
  })
  void shouldReturnCorrectApplicationByName() {
    Application application = applicationRepository.findByName("Test").get();
    assertEquals(1, application.getId());
  }

  @Test
  void shouldCorrectlySaveApplication() {
    ApplicationUploadRequest request = new ApplicationUploadRequest("Test", null, 10D);
    ApplicationMetadata metadata = new ApplicationMetadata(request, 1);
    applicationRepository.saveApplication(metadata, "path");
    Application application = applicationRepository.findByName("Test").get();
    assertTrue(
        "Test".equals(application.getName())
            && application.getPublisher().getId() == 1
            && application.getOriginalPrice() == 10
            && "path".equals(application.getPath())
    );
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, '/path/test', 1, 20, default)"
  })
  void shouldCorrectlyDeleteApplication() {
    Path path = applicationRepository.deleteApplicationById(1);
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertTrue(applicationOptional.isEmpty());
    assertEquals("/path/test", path.toString());
  }

  @Test
  void shouldReturnNullPathIfApplicationDoesntExist() {
    Path path = applicationRepository.deleteApplicationById(100);
    assertNull(path);
  }

  @Test
  @Sql(statements = {
      "insert into application values (1, 'Test', null, '/path/test', 1, 20, default)",
      "insert into application values (2, 'Test1', null, '/path/test', 1, 20, default)",
      "insert into application values (3, 'Test2', null, '/path/test', 1, 20, default)",
      "insert into application values (4, 'Test3', null, '/path/test', 2, 20, default)"
  })
  void shouldDeleteAllApplicationsByUser() {
    applicationRepository.deleteApplicationsByUser(1);
    List<Application> applications = applicationRepository.findAll();
    assertThat(applications).hasSize(1);
  }
}
