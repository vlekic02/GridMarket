package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.Price;
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

  @GetMapping(value = "/{id}/price", produces = "application/vnd.api+json")
  public DataResponse<Price> getApplicationPriceById(@PathVariable long id) {
    return DataResponse.of(applicationService.getApplicationPriceById(id));
  }
}
