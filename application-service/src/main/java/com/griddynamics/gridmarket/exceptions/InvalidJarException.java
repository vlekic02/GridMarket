package com.griddynamics.gridmarket.exceptions;

public class InvalidJarException extends BadRequestException {

  public InvalidJarException() {
    super("Provided file is not a valid jar file !");
  }
}
