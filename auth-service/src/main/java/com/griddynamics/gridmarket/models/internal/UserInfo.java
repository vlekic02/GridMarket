package com.griddynamics.gridmarket.models.internal;

import java.util.Map;

public record UserInfo(long id, String name, String surname, String username, String role,
                       double balance) {

  public Map<String, Object> getClaims() {
    return Map.of(
        "id", id,
        "name", name,
        "surname", surname,
        "username", username,
        "role", role,
        "balance", balance
    );
  }
}
