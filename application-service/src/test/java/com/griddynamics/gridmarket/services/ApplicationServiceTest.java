package com.griddynamics.gridmarket.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.griddynamics.gridmarket.exceptions.ApplicationExistsException;
import com.griddynamics.gridmarket.exceptions.BadRequestException;
import com.griddynamics.gridmarket.exceptions.InvalidUploadTokenException;
import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.repositories.impl.InMemorySetApplicationRepository;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

  private static final String UUID_REGEX =
      "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
  private static ApplicationService applicationService;
  @Mock
  public StorageService storageService;

  private static String getUploadToken() {
    ApplicationUploadRequest request = new ApplicationUploadRequest("Test1", null, 10D);
    long publisherId = 1;
    SignedUrl url = applicationService.getUploadSignedUrl(request, publisherId);
    return url.getUrl().split("=")[1];
  }

  @BeforeEach
  void setup() {
    lenient().when(storageService.save(any(MultipartFile.class), anyString(), anyLong()))
        .thenReturn(
            Path.of("test"));
    applicationService = new ApplicationService(new InMemorySetApplicationRepository(),
        storageService,
        "test");
  }

  @Test
  void shouldThrowIfNoApplicationIsPresent() {
    assertThrows(NotFoundException.class, () -> applicationService.getApplicationById(10));
  }

  @Test
  void shouldThrowIfNoApplicationPresentWhenRequestingReview() {
    assertThrows(NotFoundException.class, () -> applicationService.getAllReviewForApplication(10));
  }

  @Test
  void shouldReturnReviewForApplication() {
    Collection<Review> reviews = applicationService.getAllReviewForApplication(1);
    assertFalse(reviews.isEmpty());
  }

  @Test
  void shouldReturnApplicationIfExist() {
    Application application = applicationService.getApplicationById(1);
    assertEquals(1, application.getId());
  }

  @Test
  void shouldReturnAllApplications() {
    Collection<Application> applications = applicationService.getAllApplications();
    assertFalse(applications.isEmpty());
  }

  @Test
  void shouldCorrectlyReturnSignedUrl() {
    ApplicationUploadRequest request = new ApplicationUploadRequest("Test1", null, 10D);
    long publisherId = 1;
    SignedUrl url = applicationService.getUploadSignedUrl(request, publisherId);
    assertEquals(1, url.getId());
    assertThat(url.getUrl())
        .matches("^test/v1/applications/upload\\?token=" + UUID_REGEX + "$");
  }

  @Test
  void shouldThrowIfApplicationExist() {
    ApplicationUploadRequest request = new ApplicationUploadRequest("Test", null, 10D);
    long publisherId = 1;
    assertThrows(ApplicationExistsException.class,
        () -> applicationService.getUploadSignedUrl(request, publisherId));
  }

  @Test
  void shouldCorrectlyHandleApplicationUpload() {
    String token = getUploadToken();
    MultipartFile file = new MockMultipartFile("Test", "Content".getBytes(StandardCharsets.UTF_8));
    applicationService.handleApplicationUpload(token, file);
    Application application = applicationService.getApplicationByName("Test1");
    assertEquals("test", application.getPath());
  }

  @Test
  void shouldThrowIfFileIsEmpty() {
    String token = getUploadToken();
    MultipartFile file = new MockMultipartFile("Test", new byte[0]);
    assertThrows(BadRequestException.class, () -> {
      applicationService.handleApplicationUpload(token, file);
    });
  }

  @Test
  void shouldThrowIfInvalidTokenProvided() {
    assertThrows(InvalidUploadTokenException.class,
        () -> applicationService.handleApplicationUpload("test", null));
  }
}
