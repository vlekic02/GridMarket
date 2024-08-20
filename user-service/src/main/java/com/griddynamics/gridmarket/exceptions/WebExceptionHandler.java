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
        "Forbidden",
        HttpStatus.FORBIDDEN.value(),
        exception.getMessage()
    );
  }

  @ExceptionHandler(UnprocessableEntityException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ErrorResource handleUnprocessableEntityException(UnprocessableEntityException exception) {
    return ErrorResource.of(
        "Bad request",
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        exception.getMessage()
    );
  }

  @ExceptionHandler(InsufficientFoundsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResource handleInsufficientFoundsException(InsufficientFoundsException exception) {
    return ErrorResource.of(
        "InsufficientFounds",
        HttpStatus.BAD_REQUEST.value(),
        "You don't have enough founds for this payment"
    );
  }
}
