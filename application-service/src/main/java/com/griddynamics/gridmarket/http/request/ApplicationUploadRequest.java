package com.griddynamics.gridmarket.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ApplicationUploadRequest(
    @NotBlank(message = "Application name can't be null")
    String name,
    String description,
    @PositiveOrZero(message = "Application price can't be negative")
    @NotNull(message = "Application must have price")
    Double price
) {

}
