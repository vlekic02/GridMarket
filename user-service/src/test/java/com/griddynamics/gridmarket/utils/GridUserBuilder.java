package com.griddynamics.gridmarket.utils;

import com.griddynamics.gridmarket.models.GridUserInfo;

public final class GridUserBuilder {

  private long id;
  private String role;

  public static GridUserBuilder adminUser() {
    return new GridUserBuilder()
        .setRole("ADMIN");
  }

  public static GridUserBuilder memberUser() {
    return new GridUserBuilder()
        .setRole("MEMBER");
  }

  public GridUserBuilder setId(long id) {
    this.id = id;
    return this;
  }

  public GridUserBuilder setRole(String role) {
    this.role = role;
    return this;
  }

  public GridUserInfo build() {
    return new GridUserInfo(id, "", "", "", role, 0);
  }
}
