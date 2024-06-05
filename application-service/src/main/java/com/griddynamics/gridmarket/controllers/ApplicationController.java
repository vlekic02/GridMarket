package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.services.ApplicationService;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/applications")
@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping(produces = "application/vnd.api+json")
  public Collection<Application> getAllApplications() {
    return applicationService.getAllApplications();
  }

  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  public Application getApplicationById(@PathVariable long id) {
    return applicationService.getApplicationById(id);
  }

  @GetMapping(value = "/{applicationId}/reviews", produces = "application/vnd.api+json")
  public Collection<Review> getReviewByApplication(@PathVariable long applicationId) {
    return applicationService.getAllReviewForApplication(applicationId);
  }
}
