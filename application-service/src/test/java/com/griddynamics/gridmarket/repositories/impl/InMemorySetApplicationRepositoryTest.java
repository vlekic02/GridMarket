package com.griddynamics.gridmarket.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Optional;
import java.util.Set;
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
    assertEquals(1, applicationOptional.get().id());
  }

  @Test
  void shouldReturnAllReviewsForApplication() {
    Application application = applicationRepository.findById(1).get();
    Set<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertEquals(2, reviews.size());
  }

  @Test
  void shouldReturnEmptyCollectionIfNoReviews() {
    Application application = applicationRepository.findById(2).get();
    Set<Review> reviews = applicationRepository.findReviewsByApplication(application);
    assertEquals(0, reviews.size());
  }
}
