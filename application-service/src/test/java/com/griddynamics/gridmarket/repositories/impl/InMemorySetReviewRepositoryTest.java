package com.griddynamics.gridmarket.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ReviewRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InMemorySetReviewRepositoryTest {

  private static ReviewRepository reviewRepository;

  @BeforeAll
  static void setup() {
    reviewRepository = new InMemorySetReviewRepository();
  }

  @Test
  void shouldReturnEmptySetIfNoReviews() {
    Set<Review> reviews = reviewRepository.findByApplication(10);
    assertEquals(0, reviews.size());
  }

  @Test
  void shouldReturnCorrectReviewsForApplication() {
    Set<Review> reviews = reviewRepository.findByApplication(1);
    assertEquals(2, reviews.size());
  }
}
