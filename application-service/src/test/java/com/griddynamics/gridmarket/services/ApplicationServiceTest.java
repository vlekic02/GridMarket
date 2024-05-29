package com.griddynamics.gridmarket.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

  private static ApplicationService applicationService;
  @Mock
  private static ApplicationRepository applicationRepository;

  @BeforeEach
  void setup() {
    applicationService = new ApplicationService(applicationRepository);
  }

  @Test
  void shouldThrowIfNoApplicationIsPresent() {
    when(applicationRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> applicationService.getApplicationById(1));
  }
}
