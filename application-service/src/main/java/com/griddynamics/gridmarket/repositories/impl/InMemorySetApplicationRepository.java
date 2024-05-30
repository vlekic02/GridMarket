package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Discount.Type;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySetApplicationRepository implements ApplicationRepository {

  private final Set<Application> applications;

  public InMemorySetApplicationRepository() {
    this.applications = Set.of(
        new Application(1, "Test", null, "/system/path",
            new Discount(1, "Black friday", Type.PERCENTAGE, 20),
            25, 1),
        new Application(2, "Application 2", "Some description",
            "/system/path2", null, 15, 3),
        new Application(3, "Application 3", "Some description",
            "/system/path2", null, 50, 2),
        new Application(4, "Application 4", "Some description",
            "/system/path2", null, 5, 1)
    );
  }

  @Override
  public Set<Application> findAll() {
    return applications;
  }

  @Override
  public Optional<Application> findById(long id) {
    return applications.stream().filter(app -> app.id() == id).findFirst();
  }
}
