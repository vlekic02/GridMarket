package com.griddynamics.gridmarket.requests;

import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(
    @NotEmpty(message = "Old password field cannot be empty !") String oldPassword,
    @NotEmpty(message = "New password field cannot be empty !") String newPassword
) {

}
