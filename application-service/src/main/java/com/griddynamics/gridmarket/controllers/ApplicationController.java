package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/v1/applications")
@RestController
public class ApplicationController {

  private final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Operation(summary = "Get all available applications")
  @GetMapping(produces = "application/vnd.api+json")
  public DataResponse<Collection<Application>> getAllApplications() {
    return DataResponse.of(applicationService.getAllApplications());
  }

  @Operation(summary = "Prepare application metadata for upload")
  @PostMapping(produces = "application/vnd.api+json", consumes = MediaType.APPLICATION_JSON_VALUE)
  public DataResponse<SignedUrl> prepareApplicationMetadata(
      @Valid @RequestBody ApplicationUploadRequest request, GridUserInfo userInfo) {
    return DataResponse.of(applicationService.getUploadSignedUrl(request, userInfo.id()));
  }

  @Operation(summary = "Uploads application")
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadApplication(
      @RequestParam("file")
      MultipartFile file,
      @RequestParam("token")
      String token
  ) {
    applicationService.handleApplicationUpload(token, file);
  }

  @Operation(summary = "Get specific application by id")
  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  public DataResponse<Application> getApplicationById(@PathVariable long id) {
    return DataResponse.of(applicationService.getApplicationById(id));
  }

  @Operation(summary = "Get all reviews for specific application")
  @GetMapping(value = "/{id}/reviews", produces = "application/vnd.api+json")
  public DataResponse<Collection<Review>> getReviewByApplication(@PathVariable long id) {
    return DataResponse.of(applicationService.getAllReviewForApplication(id));
  }
}
