package com.griddynamics.gridmarket.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(UserExistsException.class)
  public String handleUserExistException(UserExistsException exception) {
    return "redirect:register?error";
  }
}
