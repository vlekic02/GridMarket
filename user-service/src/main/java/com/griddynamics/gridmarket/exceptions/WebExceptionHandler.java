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

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResource handleUnauthorizedException(UnauthorizedException exception) {
    return ErrorResource.of(
        "Unauthorized",
        HttpStatus.FORBIDDEN.value(),
        exception.getMessage()
    );
  }
}
