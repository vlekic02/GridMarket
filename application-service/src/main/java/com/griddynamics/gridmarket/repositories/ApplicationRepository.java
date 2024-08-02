package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Review;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

  List<Application> findAll();

  Optional<Application> findById(long id);

  Optional<Application> findByName(String name);

  List<Review> findReviewsByApplication(Application application);

  Path deleteApplicationById(long id);

  void deleteApplicationsByUser(long userId);

  void saveApplication(ApplicationMetadata metadata, String path);
}
