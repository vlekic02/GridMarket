package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Discount.Type;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("cloud")
@Repository
public class InMemorySetApplicationRepository implements ApplicationRepository {

  private final List<Application> applications;
  private final List<Review> reviews;
  private long lastId;

  public InMemorySetApplicationRepository() {
    this.applications = new ArrayList<>(Arrays.asList(
        new Application(1, "Test", null, "/system/path",
            Discount.unlimited(1, "Black friday", Type.PERCENTAGE, 20),
            25, 1),
        new Application(2, "Application 2", "Some description",
            "/system/path2", null, 15, 3),
        new Application(3, "Application 3", "Some description",
            "/system/path2", null, 50, 2),
        new Application(4, "Application 4", "Some description",
            "/system/path2", null, 5, 1)
    ));
    lastId = 4;
    this.reviews = new ArrayList<>(Arrays.asList(
        new Review(1, 1, 2, "Nice application", 5),
        new Review(2, 1, 4, "Meh... don't like it", 2),
        new Review(3, 3, 5, "OK", 4),
        new Review(4, 4, 8, null, 4)
    ));
  }

  @Override
  public List<Application> findAll() {
    return applications;
  }

  @Override
  public Optional<Application> findById(long id) {
    return applications.stream().filter(app -> app.getId() == id).findFirst();
  }

  @Override
  public Optional<Application> findByName(String name) {
    return applications.stream().filter(app -> app.getName().equals(name)).findFirst();
  }

  @Override
  public List<Review> findReviewsByApplication(Application application) {
    return reviews.stream().filter(review -> review.getApplication().getId() == application.getId())
        .toList();
  }

  @Override
  public Path deleteApplicationById(long id) {
    Optional<Application> applicationOptional = findById(id);
    if (applicationOptional.isPresent()) {
      Application application = applicationOptional.get();
      applications.removeIf(app -> app.getId() == id);
      return Path.of(application.getPath());
    }
    return null;
  }

  @Override
  public void deleteApplicationsByUser(long userId) {
    applications.removeIf(app -> app.getPublisher().getId() == userId);
  }

  @Override
  public void saveApplication(ApplicationMetadata metadata, String path) {
    applications.add(new Application(
        ++lastId,
        metadata.request().name(),
        metadata.request().description(),
        path,
        null,
        metadata.request().price(),
        metadata.publisherId()
    ));
  }
}
