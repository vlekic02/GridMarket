package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import java.util.Set;

public interface ReviewRepository {

  Set<Review> findByApplication(long applicationId);

  default Set<Review> findByApplication(Application application) {
    return findByApplication(application.id());
  }
}
