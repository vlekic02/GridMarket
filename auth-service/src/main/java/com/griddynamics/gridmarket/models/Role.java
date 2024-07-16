package com.griddynamics.gridmarket.models;

public class Role {

  private final long id;
  private final String name;

  public Role(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
