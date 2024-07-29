package com.griddynamics.gridmarket.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ModifyUserRequest(
    String name,
    String surname,
    String username,
    @JsonProperty("role") Long roleId,
    Double balance
) {

}
