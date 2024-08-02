package com.griddynamics.gridmarket.http.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewCreateRequest(
    String message,
    @Min(value = 1, message = "Stars count can't be lower then 1")
    @Max(value = 5, message = "Stars count can't be higher then 5")
    int stars
) {

}
