package com.griddynamics.gridmarket.requests;

import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationRequest(
    @NotEmpty(message = "Name field cannot be empty !") String name,
    @NotEmpty(message = "Surname field cannot be empty !") String surname,
    @NotEmpty(message = "Username field cannot be empty !") String username,
    @NotEmpty(message = "Password field cannot be empty !") String password
) {

}
