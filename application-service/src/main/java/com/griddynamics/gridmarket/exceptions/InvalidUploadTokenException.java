package com.griddynamics.gridmarket.exceptions;

public class InvalidUploadTokenException extends RuntimeException {

  public InvalidUploadTokenException() {
    super("Provided upload token is not valid");
  }
}
