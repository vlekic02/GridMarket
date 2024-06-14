package com.griddynamics.gridmarket.exceptions;

public class NotFoundException extends RuntimeException {

  private final long requestedResource;
  private final int status;
  private final String message;

  public NotFoundException(long requestedResource, String message) {
    super(message);
    this.requestedResource = requestedResource;
    this.status = 404;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public long getRequestedResource() {
    return requestedResource;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
