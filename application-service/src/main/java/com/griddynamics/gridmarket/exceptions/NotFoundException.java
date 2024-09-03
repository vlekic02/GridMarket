package com.griddynamics.gridmarket.exceptions;

public class NotFoundException extends RuntimeException {

  private final int status;
  private final String message;

  public NotFoundException(String message) {
    super(message);
    this.status = 404;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
