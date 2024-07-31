package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Review;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

  List<Application> findAll();

  Optional<Application> findById(long id);

  List<Review> findReviewsByApplication(Application application);

  void saveApplication(ApplicationMetadata metadata, String path);
}
