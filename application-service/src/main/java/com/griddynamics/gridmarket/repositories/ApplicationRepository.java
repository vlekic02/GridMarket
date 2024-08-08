package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Review;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

  List<Application> findAll();

  List<Application> findAll(boolean verified);

  Optional<Application> findById(long id);

  Optional<Application> findByName(String name);

  Optional<Discount> findDiscountById(long id);

  List<Review> findReviewsByApplication(Application application);

  void createReview(long applicationId, long userId, ReviewCreateRequest request);

  void verifyApplication(long id, LocalDateTime startTime, LocalDateTime endTime);

  void removeVerification(long id);

  void save(Application application);

  boolean alreadyMadeReview(long userId, long applicationId);

  void deleteReviewById(long id);

  Path deleteApplicationById(long id);

  void deleteApplicationsByUser(long userId);

  void saveApplication(ApplicationMetadata metadata, String path);
}
