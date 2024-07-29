package com.griddynamics.gridmarket.exceptions;

import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResource handleNotFoundException(
      NotFoundException exception) {
    return ErrorResource.of("Not found", exception.getStatus(), exception.getMessage());
  }
}
