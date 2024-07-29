package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Price;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public ApplicationService(ApplicationRepository applicationRepository) {
    this.applicationRepository = applicationRepository;
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

  public void deleteApplicationByUser(long userId) {
    /*TODO: placeholder, implement when implementing application CRUD*/
  }
}
