package com.griddynamics.gridmarket.model;

public record GridUser(
    long id, String name,
    String surname,
    String username,
    String role,
    double balance
) {

  @Override
  public String role() {
    return "ROLE_" + role;
  }
}
