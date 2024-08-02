package com.griddynamics.gridmarket.http.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewCreateRequest(
    String message,
    @Min(1)
    @Max(5)
    int stars
) {

}
