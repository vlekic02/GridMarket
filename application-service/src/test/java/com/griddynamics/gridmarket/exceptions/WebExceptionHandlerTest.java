package com.griddynamics.gridmarket.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WebExceptionHandlerTest {

  static WebExceptionHandler webExceptionHandler;

  @BeforeAll
  static void setup() {
    webExceptionHandler = new WebExceptionHandler();
  }

  @Test
  void testNotFoundExceptionHandler() {
    NotFoundException notFoundException = new NotFoundException("Resource not found");
    ErrorResource exceptionResponse = webExceptionHandler.handleNotFoundException(
        notFoundException);
    assertThat(exceptionResponse.getErrors()).hasSize(1);
  }
}
