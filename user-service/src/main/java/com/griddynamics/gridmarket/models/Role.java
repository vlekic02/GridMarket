package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.models.Resource;

public class Role extends Resource {

  private final String name;

  public Role(long id, String name) {
    super(id, "role");
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
