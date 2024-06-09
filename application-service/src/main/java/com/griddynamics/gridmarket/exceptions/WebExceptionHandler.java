package com.griddynamics.gridmarket.exceptions;

import com.griddynamics.gridmarket.http.response.ExceptionResponse;
import com.griddynamics.gridmarket.models.RestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionResponse handleNotFoundException(
      NotFoundException exception) {
    return ExceptionResponse.of(
        new RestException(exception.getRequestedResource(), "Not found", 404,
            exception.getMessage())
    );
  }
}
