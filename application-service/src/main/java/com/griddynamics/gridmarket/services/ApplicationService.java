package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public ApplicationService(ApplicationRepository applicationRepository) {
    this.applicationRepository = applicationRepository;
  }

  public Set<Application> getAllApplications() {
    return applicationRepository.findAll();
  }

  public Application getApplicationById(long id) {
    return applicationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Specified application not found !"));
  }
}
