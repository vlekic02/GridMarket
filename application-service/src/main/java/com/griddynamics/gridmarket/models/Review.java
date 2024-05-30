package com.griddynamics.gridmarket.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record Review(
    long id,
    long applicationId,
    @JsonIgnore long authorId,
    String message,
    int stars
) {

}
