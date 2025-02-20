package com.griddynamics.gridmarket.repositories.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.http.request.DiscountCreateRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Discount.Type;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemorySetApplicationRepositoryTest {

  private ApplicationRepository applicationRepository;

  @BeforeEach
  void setup() {
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

  @Test
  void shouldCorrectlyDeleteApplication() {
    Path path = applicationRepository.deleteApplicationById(1);
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertTrue(applicationOptional.isEmpty());
    assertEquals("/system/path", path.toString());
  }

  @Test
  void shouldReturnNullPathIfApplicationDoesntExist() {
    Path path = applicationRepository.deleteApplicationById(100);
    assertNull(path);
  }

  @Test
  void shouldGetAllApplications() {
    List<Application> applications = applicationRepository.findAll();
    assertThat(applications).hasSize(4);
  }

  @Test
  void shouldCorrectlyCreateReview() {
    ReviewCreateRequest request = new ReviewCreateRequest("Test", 5);
    applicationRepository.createReview(2, 2, request);
    Application app = applicationRepository.findById(2).get();
    List<Review> reviews = applicationRepository.findReviewsByApplication(app);
    Review review = reviews.get(0);
    assertTrue("Test".equals(review.getMessage()) && review.getStars() == 5);
  }

  @Test
  void shouldCorrectlyCheckIfUserMadeReviewForApp() {
    assertTrue(applicationRepository.alreadyMadeReview(2, 1));
  }

  @Test
  void shouldReturnFalseIfUserDidntMakeReviewForApp() {
    assertFalse(applicationRepository.alreadyMadeReview(5, 1));
  }

  @Test
  void shouldCorrectlyDeleteReview() {
    applicationRepository.deleteReviewById(3);
    Application application = applicationRepository.findById(3).get();
    List<Review> applicationReview = applicationRepository.findReviewsByApplication(application);
    assertThat(applicationReview).isEmpty();
  }

  @Test
  void shouldCorrectlyAddOwnership() {
    applicationRepository.addApplicationOwnership(1, 1);
    assertTrue(applicationRepository.hasApplicationOwnership(1, 1));
  }

  @Test
  void shouldCorrectlyReturnAllDiscountsByUser() {
    List<Discount> discounts = applicationRepository.findAllDiscountsForUser(1);
    assertThat(discounts).hasSize(1);
  }

  @Test
  void shouldCorrectlyCreateDiscountForUser() {
    var request = new DiscountCreateRequest("TestInsert",
        "PERCENTAGE", 20D, null, null);
    applicationRepository.createDiscount(request, 2);
    Discount discount = applicationRepository.findAllDiscountsForUser(2).get(0);
    assertTrue(
        discount.getName().equals("TestInsert")
            && discount.getUser().getId() == 2
            && discount.getValue() == 20D
            && discount.getDiscountType() == Type.PERCENTAGE
    );
  }

  @Test
  void shouldCorrectlyDeleteDiscount() {
    applicationRepository.deleteDiscount(1);
    Optional<Discount> discountOptional = applicationRepository.findDiscountById(1);
    assertTrue(discountOptional.isEmpty());
  }
}
