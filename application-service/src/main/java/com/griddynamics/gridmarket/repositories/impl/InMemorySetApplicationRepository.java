package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Discount.Type;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySetApplicationRepository implements ApplicationRepository {

  private final Set<Application> applications;
  private final Set<Review> reviews;

  public InMemorySetApplicationRepository() {
    this.applications = Set.of(
        new Application(1, "Test", null, "/system/path",
            new Discount(1, "Black friday", Type.PERCENTAGE, 20),
            25, 1),
        new Application(2, "Application 2", "Some description",
            "/system/path2", null, 15, 3),
        new Application(3, "Application 3", "Some description",
            "/system/path2", null, 50, 2),
        new Application(4, "Application 4", "Some description",
            "/system/path2", null, 5, 1)
    );
    this.reviews = Set.of(
        new Review(1, 1, 2, "Nice application", 5),
        new Review(2, 1, 4, "Meh... don't like it", 2),
        new Review(3, 3, 5, "OK", 4),
        new Review(4, 4, 8, null, 4)
    );
  }

  @Override
  public Set<Application> findAll() {
    return applications;
  }

  @Override
  public Optional<Application> findById(long id) {
    return applications.stream().filter(app -> app.id() == id).findFirst();
  }

  @Override
  public Set<Review> findReviewsByApplication(Application application) {
    return reviews.stream().filter(review -> review.applicationId() == application.id()).collect(
        Collectors.toUnmodifiableSet());
  }
}
