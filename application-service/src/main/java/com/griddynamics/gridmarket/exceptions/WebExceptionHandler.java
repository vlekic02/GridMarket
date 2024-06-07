package com.griddynamics.gridmarket.exceptions;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Collection<ExceptionResponse>> handleNotFoundException(
      NotFoundException exception) {
    return Map.of("errors", Collections.singletonList(
        new ExceptionResponse(exception.getRequestedResource(), "Not found", 404,
            exception.getMessage())));
  }
}
