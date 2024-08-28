package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.http.request.DiscountCreateRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Discount.Type;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Profile("cloud")
@Repository
public class InMemorySetApplicationRepository implements ApplicationRepository {

  private final Map<Long, Set<Long>> ownership;
  private final Map<Long, DateRange> verificationPeriod;
  private final List<Application> applications;
  private final List<Discount> discounts;
  private final List<Review> reviews;
  private long lastApplicationId;
  private long lastReviewId;
  private long lastDiscountId;

  public InMemorySetApplicationRepository() {
    this.verificationPeriod = new HashMap<>();
    this.discounts = new ArrayList<>(List.of(
        Discount.unlimited(1, "Black friday", Type.PERCENTAGE, 20, 1)
    ));
    lastDiscountId = 1;
    this.applications = new ArrayList<>(Arrays.asList(
        new Application(1, "Test", null, "/system/path",
            findDiscountById(1).orElseThrow(),
            25, 1, false),
        new Application(2, "Application 2", "Some description",
            "/system/path2", null, 15, 3, true),
        new Application(3, "Application 3", "Some description",
            "/system/path2", null, 50, 2, true),
        new Application(4, "Application 4", "Some description",
            "/system/path2", null, 5, 1, false)
    ));
    lastApplicationId = 4;
    verificationPeriod.put(2L, DateRange.empty());
    verificationPeriod.put(3L, DateRange.empty());
    this.reviews = new ArrayList<>(Arrays.asList(
        new Review(1, 1, 2, "Nice application", 5),
        new Review(2, 1, 4, "Meh... don't like it", 2),
        new Review(3, 3, 5, "OK", 4),
        new Review(4, 4, 8, null, 4)
    ));
    lastApplicationId = 4;
    this.ownership = new HashMap<>();
  }

  @Override
  public List<Application> findAll() {
    return applications;
  }

  @Override
  public List<Application> findAll(boolean verified, Pageable pageable) {
    return applications.stream()
        .filter(app -> app.isVerified() == verified)
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .toList();
  }

  @Override
  public List<Application> findBySearchKey(boolean verified, String searchKey, Pageable pageable) {
    return applications.stream()
        .filter(app -> app.isVerified() == verified)
        .filter(app -> {
          if (app.getName().toLowerCase().contains(searchKey)) {
            return true;
          }
          return app.getDescription() != null && app.getDescription().toLowerCase()
              .contains(searchKey);
        })
        .sorted((first, second) -> {
          int firsCount = findReviewsByApplication(first).size();
          int secondCount = findReviewsByApplication(second).size();
          return Integer.compare(firsCount, secondCount);
        })
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .toList();
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
  public Optional<Discount> findDiscountById(long id) {
    return discounts.stream().filter(discount -> discount.getId() == id).findFirst();
  }

  @Override
  public List<Review> findReviewsByApplication(Application application) {
    return reviews.stream().filter(review -> review.getApplication().getId() == application.getId())
        .toList();
  }

  @Override
  public void createReview(long applicationId, long userId, ReviewCreateRequest request) {
    this.reviews.add(new Review(
        ++lastReviewId,
        applicationId,
        userId,
        request.message(),
        request.stars()
    ));
  }

  @Override
  public void verifyApplication(long id, LocalDateTime startTime, LocalDateTime endTime) {
    findById(id).ifPresent(app -> {
      deleteApplicationById(app.getId());
      Application newApp = app.builder().setVerified(true).build();
      applications.add(newApp);
      verificationPeriod.put(app.getId(), new DateRange(startTime, endTime));
    });
  }

  @Override
  public void removeVerification(long id) {
    findById(id).ifPresent(app -> {
      deleteApplicationById(app.getId());
      Application newApp = app.builder().setVerified(false).build();
      applications.add(newApp);
      verificationPeriod.remove(app.getId());
    });
  }

  @Override
  public void save(Application application) {
    deleteApplicationById(application.getId());
    applications.add(application);
  }

  @Override
  public boolean alreadyMadeReview(long userId, long applicationId) {
    return reviews.stream()
        .anyMatch(review -> review.getApplication().getId() == applicationId
            && review.getAuthor().getId() == userId);
  }

  @Override
  public boolean hasApplicationOwnership(long userId, long applicationId) {
    Set<Long> applications = ownership.get(userId);
    if (applications == null) {
      return false;
    }
    return applications.contains(applicationId);
  }

  @Override
  public void deleteReviewById(long id) {
    this.reviews.removeIf(review -> review.getId() == id);
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
        ++lastApplicationId,
        metadata.request().name(),
        metadata.request().description(),
        path,
        null,
        metadata.request().price(),
        metadata.publisherId(),
        false
    ));
  }

  @Override
  public void addApplicationOwnership(long userId, long applicationId) {
    ownership.computeIfAbsent(userId, (key) -> new HashSet<>())
        .add(applicationId);
  }

  @Override
  public void createDiscount(DiscountCreateRequest request, long userId) {
    discounts.add(new Discount(
        ++lastDiscountId,
        request.name(),
        Discount.Type.valueOf(request.type()),
        request.value(),
        request.startTime(),
        request.endTime(),
        userId
    ));
  }

  private record DateRange(LocalDateTime startTime, LocalDateTime endTime) {

    public static DateRange empty() {
      return new DateRange(null, null);
    }
  }
}
