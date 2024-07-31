package com.griddynamics.gridmarket.exceptions;

public class ApplicationExistsException extends BadRequestException {

  public ApplicationExistsException(String name) {
    super("Application with name " + name + " already exists");
  }
}
