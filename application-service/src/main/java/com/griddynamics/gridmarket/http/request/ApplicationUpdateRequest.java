package com.griddynamics.gridmarket.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public record ApplicationUpdateRequest(
    String name,
    String description,
    @Min(value = 0, message = "Price must be greater then or equal to 0")
    Double price,
    @JsonProperty("discount")
    Long discountId,
    @Valid
    VerifyRequest verify
) {

}
