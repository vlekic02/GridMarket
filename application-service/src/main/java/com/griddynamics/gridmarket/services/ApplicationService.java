package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Price;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.models.SignedUrl;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

  private static final int TOKEN_LIFETIME = 5;
  private static final String UPLOAD_PATH = "/v1/applications/upload?token=";

  private final ApplicationRepository applicationRepository;
  private final Map<String, ApplicationMetadata> tokenMap;
  private final ScheduledExecutorService executorService;
  private final String baseUrl;

  public ApplicationService(ApplicationRepository applicationRepository,
      @Value("${base-path}") String baseUrl) {
    this.applicationRepository = applicationRepository;
    this.tokenMap = new ConcurrentHashMap<>();
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.baseUrl = baseUrl;
  }

  public Collection<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  public Application getApplicationById(long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(id, "Specified application not found !"));
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

  public SignedUrl getUploadSignedUrl(ApplicationUploadRequest request, long publishedId) {
    String token = UUID.randomUUID().toString();
    ApplicationMetadata metadata = new ApplicationMetadata(request, publishedId);
    tokenMap.put(token, metadata);
    executorService.schedule(() -> tokenMap.remove(token), TOKEN_LIFETIME, TimeUnit.MINUTES);
    String signedUrl = baseUrl + UPLOAD_PATH + token;
    return new SignedUrl(publishedId, signedUrl);
  }

  public void deleteApplicationByUser(long userId) {
    /*TODO: placeholder, implement when implementing application CRUD*/
  }
}
