package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import java.util.Optional;
import java.util.Set;

public interface ApplicationRepository {

  Set<Application> findAll();

  Optional<Application> findById(long id);

  Set<Review> findReviewsByApplication(Application application);
}
