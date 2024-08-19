package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationInfo;
import com.griddynamics.gridmarket.services.ApplicationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/internal")
@RestController
public class InternalController {

  private final ApplicationService applicationService;

  public InternalController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping(value = "/{id}/info", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApplicationInfo getApplicationInfoById(
      @PathVariable long id,
      @RequestParam("ownership") long userId
  ) {
    Application application = applicationService.getApplicationById(id);
    boolean ownership = applicationService.checkApplicationOwnership(userId, id);
    return new ApplicationInfo(
        application.getPublisher().getId(),
        application.getRealPrice(),
        ownership
    );
  }
}
