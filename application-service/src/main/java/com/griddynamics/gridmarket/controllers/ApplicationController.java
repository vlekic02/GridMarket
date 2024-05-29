package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.services.ApplicationService;
import com.griddynamics.gridmarket.services.ReviewService;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/applications")
@RestController
public class ApplicationController {

  private final ApplicationService applicationService;
  private final ReviewService reviewService;

  public ApplicationController(ApplicationService applicationService, ReviewService reviewService) {
    this.applicationService = applicationService;
    this.reviewService = reviewService;
  }

  @GetMapping(value = {"/", ""}, produces = "application/json")
  public Set<Application> getAllApplications() {
    return applicationService.getAllApplications();
  }

  @GetMapping(value = {"/{id}/",
      "/{id}"}, produces = "application/json")
  public Application getApplicationById(@PathVariable long id) {
    return applicationService.getApplicationById(id);
  }

  @GetMapping(value = {"/{applicationId}/reviews/",
      "/{applicationId}/reviews"}, produces = "application/json")
  public Set<Review> getReviewByApplication(@PathVariable long applicationId) {
    Application application = applicationService.getApplicationById(applicationId);
    return reviewService.getReviewByApplication(application);
  }
}
