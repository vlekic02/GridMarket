package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ReviewRepository;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;

  public ReviewService(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  public Set<Review> getReviewByApplication(Application application) {
    return reviewRepository.findByApplication(application);
  }
}
