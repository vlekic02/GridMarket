package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationInfo;
import com.griddynamics.gridmarket.services.ApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/internal")
@RestController
public class InternalController {

  private final ApplicationService applicationService;

  public InternalController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping(value = "/{id}/info", produces = "application/vnd.api+json")
  public ApplicationInfo getApplicationInfoById(@PathVariable long id) {
    Application application = applicationService.getApplicationById(id);
    return new ApplicationInfo(application.getPublisher().getId(), application.getRealPrice());
  }
}
