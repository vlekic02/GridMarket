package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.annotations.AdminAccess;
import com.griddynamics.gridmarket.http.request.ApplicationUpdateRequest;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.http.request.DiscountCreateRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  @Operation(summary = "Get all applications")
  @GetMapping(produces = "application/vnd.api+json")
  public DataResponse<Collection<Application>> getAllApplications(
      @RequestParam(name = "verified", defaultValue = "true")
      @Parameter(in = ParameterIn.QUERY,
          description = "Specifies which type of apps it should return, "
              + "only admin can see unverified ones"
      )
      boolean verified,
      @RequestParam(name = "search", required = false)
      @Parameter(in = ParameterIn.QUERY, description = "Key for application searching")
      String searchKey,
      Pageable pageable,
      GridUserInfo userInfo
  ) {
    return DataResponse.of(
        applicationService.getAllApplications(verified, searchKey, pageable, userInfo)
    );
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
      @Parameter(in = ParameterIn.QUERY,
          description = "Upload token received from POST /v1/applications")
      String token
  ) {
    applicationService.handleApplicationUpload(token, file);
  }

  @Operation(summary = "Get specific application by id")
  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  public DataResponse<Application> getApplicationById(@PathVariable long id,
      GridUserInfo userInfo) {
    return DataResponse.of(applicationService.getApplicationById(id, userInfo));
  }

  @Operation(summary = "Updates specific application")
  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateApplication(
      @PathVariable long id,
      @Valid @RequestBody ApplicationUpdateRequest request,
      GridUserInfo userInfo
  ) {
    applicationService.updateApplication(id, request, userInfo);
  }

  @Operation(summary = "Deletes specific application")
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteApplication(@PathVariable long id, GridUserInfo userInfo) {
    applicationService.deleteApplication(id, userInfo);
  }

  @Operation(summary = "Downloads file by id")
  @GetMapping(value = "/{id}/pull", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public FileSystemResource pullApplication(
      @PathVariable long id,
      GridUserInfo userInfo,
      HttpServletResponse response
  ) {
    FileSystemResource fileSystemResource = applicationService.pullApplication(id, userInfo);
    ContentDisposition contentDisposition = ContentDisposition
        .attachment()
        .filename(fileSystemResource.getFilename())
        .build();
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
    return fileSystemResource;
  }

  @Operation(summary = "Get all reviews for specific application")
  @GetMapping(value = "/{id}/reviews", produces = "application/vnd.api+json")
  public DataResponse<Collection<Review>> getReviewByApplication(@PathVariable long id,
      GridUserInfo userInfo) {
    return DataResponse.of(applicationService.getAllReviewForApplication(id, userInfo));
  }

  @Operation(summary = "Create new review for specific application")
  @PostMapping(value = "/{id}/reviews")
  @ResponseStatus(HttpStatus.CREATED)
  public void createReview(
      @PathVariable long id,
      @RequestBody @Valid ReviewCreateRequest request,
      GridUserInfo userInfo
  ) {
    applicationService.createReview(id, request, userInfo);
  }

  @Operation(summary = "Deletes a specific review")
  @DeleteMapping("/reviews/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @AdminAccess
  public void deleteReview(@PathVariable long id) {
    applicationService.deleteReview(id);
  }

  @Operation(summary = "Creates a discount")
  @PostMapping("/discounts")
  @ResponseStatus(HttpStatus.CREATED)
  public void createDiscount(
      @RequestBody @Valid DiscountCreateRequest request,
      GridUserInfo userInfo
  ) {
    applicationService.createDiscount(request, userInfo);
  }

  @Operation(summary = "List all discounts for a user")
  @GetMapping("/discounts")
  public DataResponse<Collection<Discount>> getAllDiscountsForUser(GridUserInfo userInfo) {
    return DataResponse.of(applicationService.getAllDiscountsForUser(userInfo));
  }

  @Operation(summary = "Delete specific discount by user")
  @DeleteMapping("/discounts/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDiscountForUser(@PathVariable long id, GridUserInfo userInfo) {
    applicationService.deleteDiscount(id, userInfo);
  }
}
