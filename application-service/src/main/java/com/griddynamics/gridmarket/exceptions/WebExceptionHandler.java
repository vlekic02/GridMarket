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
  public ErrorResource handlerMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    String validationErrors = exception.getBindingResult().getFieldErrors()
        .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining("\n"));
    return ErrorResource.of(
        "Bad request", HttpStatus.BAD_REQUEST.value(), validationErrors);
  }
}
