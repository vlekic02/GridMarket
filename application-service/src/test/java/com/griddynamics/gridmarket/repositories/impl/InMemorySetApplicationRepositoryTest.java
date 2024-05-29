package com.griddynamics.gridmarket.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InMemorySetApplicationRepositoryTest {

  private static ApplicationRepository applicationRepository;

  @BeforeAll
  static void setup() {
    applicationRepository = new InMemorySetApplicationRepository();
  }

  @Test
  void shouldReturnEmptyOptionalIfInvalidId() {
    Optional<Application> applicationOptional = applicationRepository.findById(10);
    assertTrue(applicationOptional.isEmpty());
  }

  @Test
  void shouldReturnApplicationIfIdIsValid() {
    Optional<Application> applicationOptional = applicationRepository.findById(1);
    assertEquals(1, applicationOptional.get().getId());
  }
}
