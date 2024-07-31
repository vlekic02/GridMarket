package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.ApplicationExistsException;
import com.griddynamics.gridmarket.exceptions.BadRequestException;
import com.griddynamics.gridmarket.exceptions.InvalidUploadTokenException;
import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApplicationService {

  private static final int TOKEN_LIFETIME = 5;
  private static final String UPLOAD_URI = "/v1/applications/upload?token=";

  private final ApplicationRepository applicationRepository;
  private final StorageService storageService;
  private final Map<String, ApplicationMetadata> tokenMap;
  private final ScheduledExecutorService executorService;
  private final String baseUrl;

  public ApplicationService(ApplicationRepository applicationRepository,
      StorageService storageService,
      @Value("${base-path}") String baseUrl) {
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

  public Collection<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  public Application getApplicationById(long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Specified application not found !"));
  }

  public Collection<Review> getAllReviewForApplication(long applicationId) {
    Application application = getApplicationById(applicationId);
    return applicationRepository.findReviewsByApplication(application);
  }

  public Price getApplicationPriceById(long id) {
    Application application = getApplicationById(id);
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

  public void deleteApplicationByUser(long userId) {
    /*TODO: placeholder, implement when implementing application CRUD*/
  }
}
