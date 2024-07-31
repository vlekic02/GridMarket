package com.griddynamics.gridmarket.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InMemorySetApplicationRepositoryTest {

  private static ApplicationRepository applicationRepository;

  @BeforeAll
  static void setup() {
    applicationRepository = new InMemorySetApplicationRepository();
  }

  @Test
  void shouldReturnEmptyOptionalIfInvalidId() {
    Optional<Application> applicationOptional = applicationRepository.findById(10);
    assertTrue(applicationOptional.isEmpty());
  }

  @Test
  void shouldReturnApplicationIfIdIsValid() {
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertEquals(1, applicationOptional.get().getId());
  }

  @Test
  void shouldReturnAllReviewsForApplication() {
    Application application = applicationRepository.findById(1).get();
    List<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertEquals(2, reviews.size());
  }

  @Test
  void shouldReturnEmptyCollectionIfNoReviews() {
    Application application = applicationRepository.findById(2).get();
    List<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertEquals(0, reviews.size());
  }

  @Test
  void shouldCorrectlyFindApplicationByName() {
    Application application = applicationRepository.findByName("Application 2").get();
    assertEquals(2, application.getId());
  }

  @Test
  void shouldCorrectlySaveApplication() {
    ApplicationUploadRequest request = new ApplicationUploadRequest("Test1", null, 10D);
    ApplicationMetadata metadata = new ApplicationMetadata(request, 1);
    applicationRepository.saveApplication(metadata, "path");
    Application application = applicationRepository.findByName("Test1").get();
    assertTrue(
        "Test1".equals(application.getName())
            && application.getPublisher().getId() == 1
            && application.getOriginalPrice() == 10
            && "path".equals(application.getPath())
    );
  }
}
