package com.griddynamics.gridmarket.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.impl.InMemorySetApplicationRepository;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationServiceTest {

  private static ApplicationService applicationService;

  @BeforeEach
  void setup() {
    applicationService = new ApplicationService(new InMemorySetApplicationRepository());
  }

  @Test
  void shouldThrowIfNoApplicationIsPresent() {
    assertThrows(NotFoundException.class, () -> applicationService.getApplicationById(10));
  }

  @Test
  void shouldThrowIfNoApplicationPresentWhenRequestingReview() {
    assertThrows(NotFoundException.class, () -> applicationService.getAllReviewForApplication(10));
  }

  @Test
  void shouldReturnReviewForApplication() {
    Collection<Review> reviews = applicationService.getAllReviewForApplication(1);
    assertFalse(reviews.isEmpty());
  }

  @Test
  void shouldReturnApplicationIfExist() {
    Application application = applicationService.getApplicationById(1);
    assertEquals(1, application.getId());
  }

  @Test
  void shouldReturnAllApplications() {
    Collection<Application> applications = applicationService.getAllApplications();
    assertFalse(applications.isEmpty());
  }
}
