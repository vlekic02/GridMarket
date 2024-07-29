package com.griddynamics.gridmarket.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModifyUserRequest(
    String name,
    String surname,
    String username,
    @JsonProperty("role") long roleId,
    double balance
) {

}
