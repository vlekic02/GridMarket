package com.griddynamics.gridmarket.exceptions;

public class UserExistsException extends RuntimeException {

  private final String username;

  public UserExistsException(String username) {
    super("User with username " + username + " already exist !");
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
