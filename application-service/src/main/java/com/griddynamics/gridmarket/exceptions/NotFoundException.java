package com.griddynamics.gridmarket.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

  private final int status;
  private final String message;

  public NotFoundException(String message) {
    super(message);
    this.status = 404;
    this.message = message;
  }
}
