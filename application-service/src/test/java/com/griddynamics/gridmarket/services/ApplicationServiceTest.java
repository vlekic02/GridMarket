package com.griddynamics.gridmarket.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.griddynamics.gridmarket.exceptions.ApplicationExistsException;
import com.griddynamics.gridmarket.exceptions.BadRequestException;
import com.griddynamics.gridmarket.exceptions.InvalidUploadTokenException;
import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.exceptions.UnprocessableEntityException;
import com.griddynamics.gridmarket.http.request.ApplicationUpdateRequest;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.http.request.VerifyRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.pubsub.event.OrderSuccessEvent;
import com.griddynamics.gridmarket.repositories.impl.InMemorySetApplicationRepository;
import com.griddynamics.gridmarket.utils.GridUserBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
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

  private static Stream<GridUserInfo> getUserInfo() {
    return Stream.of(
        GridUserBuilder.adminUser().setId(2).build(), //ADMIN
        GridUserBuilder.memberUser().setId(1).build() // Resource owner
    );
  }

  private static Stream<GridUserInfo> getPullApplicationUserInfo() {
    return Stream.of(
        GridUserBuilder.adminUser().setId(10).build(), // ADMIN
        GridUserBuilder.memberUser().setId(3).build(),  // App publisher
        GridUserBuilder.memberUser().setId(8).build() // Have purchased an app
    );
  }

  @BeforeEach
  void setup() {
    lenient().when(storageService.save(any(MultipartFile.class), anyString(), anyLong()))
        .thenReturn(
            Path.of("test"));
    lenient().when(storageService.getFileByPath(anyString()))
        .thenReturn(new FileSystemResource("test"));
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
    GridUserInfo gridUserInfo = GridUserBuilder.memberUser().build();
    assertThrows(NotFoundException.class,
        () -> applicationService.getAllReviewForApplication(10, gridUserInfo));
  }

  @Test
  void shouldReturnReviewForApplication() {
    GridUserInfo gridUserInfo = GridUserBuilder.memberUser().build();
    Collection<Review> reviews = applicationService.getAllReviewForApplication(3, gridUserInfo);
    assertFalse(reviews.isEmpty());
  }

  @Test
  void shouldReturnApplicationIfExist() {
    Application application = applicationService.getApplicationById(1);
    assertEquals(1, application.getId());
  }

  @Test
  void shouldReturnAllVerifiedApplications() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().build();
    Collection<Application> applications = applicationService.getAllApplications(true, null,
        PageRequest.of(0, 30),
        userInfo);
    assertThat(applications).hasSize(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"app", "desc"})
  void shouldReturnAllUnverifiedAppsBySearchKey(String key) {
    GridUserInfo userInfo = GridUserBuilder.adminUser().build();
    Collection<Application> applications = applicationService.getAllApplications(false, key,
        PageRequest.of(0, 30),
        userInfo);
    assertThat(applications).hasSize(1).satisfies(apps -> {
      Application app = apps.iterator().next();
      assertEquals(4, app.getId());
    });
  }

  @Test
  void shouldReturnVerifiedAppsIfUserIsNotAdmin() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().build();
    Collection<Application> applications = applicationService.getAllApplications(false, null,
        PageRequest.of(0, 30),
        userInfo);
    assertThat(applications)
        .hasSize(2)
        .allMatch(Application::isVerified);
  }

  @Test
  void shouldReturnAllUnverifiedApps() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().build();
    Collection<Application> applications = applicationService.getAllApplications(false, null,
        PageRequest.of(0, 30),
        userInfo);
    assertThat(applications)
        .hasSize(2)
        .allMatch(app -> !app.isVerified());
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
  void shouldThrowIfApplicationNameDoesntExist() {
    assertThrows(NotFoundException.class, () -> {
      applicationService.getApplicationByName("TestTest");
    });
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

  @ParameterizedTest
  @MethodSource("getUserInfo")
  void shouldCorrectlyDeleteApplication(GridUserInfo userInfo) {
    applicationService.deleteApplication(1, userInfo);
    assertThrows(NotFoundException.class, () -> applicationService.getApplicationById(1));
  }

  @Test
  void shouldThrowIfUnauthorizedUserTryToDeleteApplication() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().build();
    assertThrows(UnauthorizedException.class,
        () -> applicationService.deleteApplication(1, userInfo));
  }

  @Test
  void shouldThrowWhenCreatingReviewIfInvalidApplication() {
    assertThrows(NotFoundException.class,
        () -> applicationService.createReview(10, new ReviewCreateRequest("", 5), null));
  }

  @Test
  void shouldThrowIfApplicationIsNotVerified() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().build();
    assertThrows(NotFoundException.class,
        () -> applicationService.createReview(4, new ReviewCreateRequest("", 5), userInfo));
  }

  @Test
  void shouldThrowIfAppPublisherAndReviewAuthorAreSame() {
    assertThrows(BadRequestException.class,
        () -> applicationService.createReview(2, new ReviewCreateRequest("", 5),
            GridUserBuilder.memberUser().setId(3).build()));
  }

  @Test
  void shouldThrowIfUserAlreadyMadeRequest() {
    assertThrows(BadRequestException.class,
        () -> applicationService.createReview(3, new ReviewCreateRequest("", 5),
            GridUserBuilder.memberUser().setId(5).build()));
  }

  @Test
  void shouldCorrectlyCreateReview() {
    GridUserInfo gridUserInfo = GridUserBuilder.memberUser().setId(10).build();
    applicationService.createReview(2, new ReviewCreateRequest("Test", 5),
        gridUserInfo);

    List<Review> reviews = (List<Review>) applicationService.getAllReviewForApplication(2,
        gridUserInfo);
    Review review = reviews.get(0);
    assertTrue("Test".equals(review.getMessage()) && review.getStars() == 5);
  }

  @Test
  void shouldCorrectlyDeleteReview() {
    applicationService.deleteReview(3);
    GridUserInfo gridUserInfo = GridUserBuilder.memberUser().build();
    Collection<Review> reviewList = applicationService.getAllReviewForApplication(3, gridUserInfo);
    assertThat(reviewList).isEmpty();
  }

  @Test
  void shouldThrowIfMemberTryToChangeVerificationStatus() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().build();
    ApplicationUpdateRequest request = new ApplicationUpdateRequest(
        "", "", 1D, 1L, new VerifyRequest(true, null, null));
    assertThrows(UnauthorizedException.class,
        () -> applicationService.updateApplication(1, request, userInfo));
  }

  @Test
  void shouldTrowIfUnauthorizedMemberTryToUpdateApp() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().setId(1).build();
    ApplicationUpdateRequest request = new ApplicationUpdateRequest(
        "", "", 1D, 1L, null);
    assertThrows(UnauthorizedException.class,
        () -> applicationService.updateApplication(2, request, userInfo));
  }

  @Test
  void shouldCorrectlyUpdateApplication() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest("TestName",
        "TestDesc", 10D, 1L, null);
    applicationService.updateApplication(2, applicationUpdateRequest, userInfo);
    Application application = applicationService.getApplicationById(2);
    assertTrue(
        "TestName".equals(application.getName())
            && "TestDesc".equals(application.getDescription())
            && application.getOriginalPrice() == 10
            && application.getDiscount().getId() == 1
    );
  }

  @Test
  void shouldCorrectlyRemoveApplicationDiscount() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest(null,
        null, null, 1L, null);
    applicationService.updateApplication(1, applicationUpdateRequest, userInfo);
    Application application = applicationService.getApplicationById(2);
    assertNull(application.getDiscount());
  }

  @Test
  void shouldThrowIfNewApplicationNameAlreadyPresent() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest("Test",
        "TestDesc", 10D, null, null);
    assertThrows(UnprocessableEntityException.class,
        () -> applicationService.updateApplication(2, applicationUpdateRequest, userInfo));
  }

  @Test
  void shouldThrowIfInvalidDiscoundId() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest("TestName",
        "TestDesc", 10D, 10L, null);
    assertThrows(UnprocessableEntityException.class,
        () -> applicationService.updateApplication(2, applicationUpdateRequest, userInfo));
  }

  @Test
  void shouldCorrectlyVerifyApplication() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest(null,
        null, null, null, new VerifyRequest(true, null, null));
    applicationService.updateApplication(1, applicationUpdateRequest, userInfo);
    Application application = applicationService.getApplicationById(1);
    assertTrue(application.isVerified());
  }

  @Test
  void shouldCorrectlyRemoveAppVerification() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(1).build();
    ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest(null,
        null, null, null, new VerifyRequest(false, null, null));
    applicationService.updateApplication(2, applicationUpdateRequest, userInfo);
    Application application = applicationService.getApplicationById(2);
    assertFalse(application.isVerified());
  }

  @Test
  void shouldThrowIfUnauthorizedUserTryToPullApplication() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().setId(1).build();
    assertThrows(UnauthorizedException.class,
        () -> applicationService.pullApplication(2, userInfo));
  }

  @ParameterizedTest
  @MethodSource("getPullApplicationUserInfo")
  void shouldCorrectlyPullApplication(GridUserInfo userInfo) {
    applicationService.handleOrderSuccess(new OrderSuccessEvent(8, 2));
    FileSystemResource systemResource = applicationService.pullApplication(2, userInfo);
    assertEquals("test", systemResource.getPath());
  }

  @Test
  void shouldThrowIfInvalidUserTryToDeleteDiscount() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().setId(2).build();
    assertThrows(UnauthorizedException.class, () ->
        applicationService.deleteDiscount(1, userInfo));
  }

  @Test
  void shouldCorrectlyDeleteIfUserMadeDiscount() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().setId(1).build();
    applicationService.deleteDiscount(1, userInfo);
  }

  @Test
  void shouldCorrectlyDeleteIfUserIsAdmin() {
    GridUserInfo userInfo = GridUserBuilder.adminUser().setId(10).build();
    applicationService.deleteDiscount(1, userInfo);
  }
}
