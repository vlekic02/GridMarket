package com.griddynamics.gridmarket.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import com.griddynamics.gridmarket.http.response.ExceptionResponse;
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
    NotFoundException notFoundException = new NotFoundException(10, "Resource not found");
    ExceptionResponse exceptionResponse = webExceptionHandler.handleNotFoundException(
        notFoundException);
    assertThat(exceptionResponse.getErrors()).hasSize(1);
  }
}
