package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.ApplicationExistsException;
import com.griddynamics.gridmarket.exceptions.BadRequestException;
import com.griddynamics.gridmarket.exceptions.InvalidUploadTokenException;
import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.Price;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApplicationService {

  private static final int TOKEN_LIFETIME = 5;
  private static final String ADMIN_ROLE = "ADMIN";
  private static final String UPLOAD_URI = "/v1/applications/upload?token=";

  private final ApplicationRepository applicationRepository;
  private final StorageService storageService;
  private final Map<String, ApplicationMetadata> tokenMap;
  private final ScheduledExecutorService executorService;
  private final String baseUrl;

  public ApplicationService(
      ApplicationRepository applicationRepository,
      StorageService storageService,
      @Value("${base-path}") String baseUrl
  ) {
    this.applicationRepository = applicationRepository;
    this.storageService = storageService;
    this.tokenMap = new ConcurrentHashMap<>();
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.baseUrl = baseUrl;
  }

  public Application getApplicationByName(String name) {
    return applicationRepository.findByName(name)
        .orElseThrow(() -> new NotFoundException("Specified application not found"));
  }

  //TODO REMOVE
  public Collection<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  public Collection<Application> getAllApplications(boolean verified, GridUserInfo userInfo) {
    if (!verified && isNotAdmin(userInfo)) {
      verified = true;
    }
    return applicationRepository.findAll(verified);
  }

  public FileSystemResource pullApplication(long id, GridUserInfo userInfo) {
    Application application = getApplicationById(id, userInfo);
    return storageService.getFileByPath(application.getPath());
  }

  public Application getApplicationById(long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Specified application not found !"));
  }

  public Application getApplicationById(long id, GridUserInfo userInfo) {
    return applicationRepository.findById(id)
        .filter(app -> app.isVerified() || ADMIN_ROLE.equals(userInfo.role()))
        .orElseThrow(() -> new NotFoundException("Specified application not found !"));
  }

  public Collection<Review> getAllReviewForApplication(long applicationId, GridUserInfo userInfo) {
    Application application = getApplicationById(applicationId, userInfo);
    return applicationRepository.findReviewsByApplication(application);
  }

  public Price getApplicationPriceById(long id) {
    Application application = getApplicationById(id);
    if (!application.isVerified()) {
      throw new NotFoundException("Specified application not found !");
    }
    double price = application.getRealPrice();
    return new Price(id, price);
  }

  public void handleApplicationUpload(String token, MultipartFile file) {
    ApplicationMetadata applicationMetadata = tokenMap.get(token);
    tokenMap.remove(token);
    if (applicationMetadata == null) {
      throw new InvalidUploadTokenException();
    }
    if (file.isEmpty()) {
      throw new BadRequestException("Uploaded file can't be empty !");
    }
    Path path = storageService.save(file, file.getOriginalFilename(),
        applicationMetadata.publisherId());
    applicationRepository.saveApplication(applicationMetadata, path.toString());
    tokenMap.remove(token);
  }

  public SignedUrl getUploadSignedUrl(ApplicationUploadRequest request, long publishedId) {
    applicationRepository.findByName(request.name()).ifPresent(app -> {
      throw new ApplicationExistsException(app.getName());
    });
    String token = UUID.randomUUID().toString();
    ApplicationMetadata metadata = new ApplicationMetadata(request, publishedId);
    tokenMap.put(token, metadata);
    executorService.schedule(() -> tokenMap.remove(token), TOKEN_LIFETIME, TimeUnit.MINUTES);
    String signedUrl = baseUrl + UPLOAD_URI + token;
    return new SignedUrl(publishedId, signedUrl);
  }

  public void deleteApplication(long id, GridUserInfo userInfo) {
    applicationRepository.findById(id).ifPresent(app -> {
      if (isNotAdmin(userInfo) && userInfo.id() != app.getPublisher().getId()) {
        throw new UnauthorizedException("You don't have permission to delete this application");
      }
      Path applicationPath = applicationRepository.deleteApplicationById(id);
      storageService.delete(applicationPath);
    });
  }

  public void deleteApplicationByUser(long userId) {
    applicationRepository.deleteApplicationsByUser(userId);
    storageService.deleteByUser(userId);
  }

  public void createReview(long applicationId, ReviewCreateRequest request, GridUserInfo userInfo) {
    Application application = getApplicationById(applicationId, userInfo);
    if (application.getPublisher().getId() == userInfo.id()) {
      throw new BadRequestException("You can't review your own application");
    }
    if (applicationRepository.alreadyMadeReview(userInfo.id(), applicationId)) {
      throw new BadRequestException("You already reviewed this application");
    }
    applicationRepository.createReview(applicationId, userInfo.id(), request);
  }

  public void deleteReview(long id) {
    applicationRepository.deleteReviewById(id);
  }

  private boolean isNotAdmin(GridUserInfo userInfo) {
    return !ADMIN_ROLE.equals(userInfo.role());
  }
}
