package com.griddynamics.gridmarket.exceptions;

import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(UserExistsException.class)
  public String handleUserExistException(UserExistsException exception) {
    return "redirect:register?error";
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResource handleValidationFailure(MethodArgumentNotValidException exception) {
    return ErrorResource.of(
        "Bad request",
        HttpStatus.BAD_REQUEST.value(),
        exception.getMessage()
    );
  }
}
