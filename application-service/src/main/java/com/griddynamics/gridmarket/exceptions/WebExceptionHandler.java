package com.griddynamics.gridmarket.exceptions;

import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResource handleNotFoundException(
      NotFoundException exception) {
    return ErrorResource.of("Not found", HttpStatus.NOT_FOUND.value(), exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResource handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    String validationErrors = exception.getBindingResult().getFieldErrors()
        .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining("\n"));
    return ErrorResource.of(
        "Bad request", HttpStatus.BAD_REQUEST.value(), validationErrors);
  }

  @ExceptionHandler(InvalidUploadTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ErrorResource handleInvalidUploadTokenException(InvalidUploadTokenException exception) {
    return ErrorResource.of(
        "Invalid token", HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
  }

  @ExceptionHandler(InternalServerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResource handleInternalServerException(InternalServerException exception) {
    return ErrorResource.of(
        "Internal server error",
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        exception.getMessage()
    );
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResource handleBadRequestException(BadRequestException exception) {
    return ErrorResource.of(
        "Bad request",
        HttpStatus.BAD_REQUEST.value(),
        exception.getMessage()
    );
  }
}
