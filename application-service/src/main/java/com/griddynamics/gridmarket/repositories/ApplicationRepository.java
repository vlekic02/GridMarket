package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Application;
import java.util.Optional;
import java.util.Set;

public interface ApplicationRepository {

  Set<Application> findAll();

  Optional<Application> findById(long id);
}
