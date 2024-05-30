package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ReviewRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySetReviewRepository implements ReviewRepository {

  private final Set<Review> reviews;

  public InMemorySetReviewRepository() {
    this.reviews = Set.of(
        new Review(1, 1, 2, "Nice application", 5),
        new Review(2, 1, 4, "Meh... don't like it", 2),
        new Review(3, 3, 5, "OK", 4),
        new Review(4, 4, 8, null, 4)
    );
  }

  @Override
  public Set<Review> findByApplication(long applicationId) {
    return reviews.stream().filter(review -> review.applicationId() == applicationId).collect(
        Collectors.toUnmodifiableSet());
  }
}
