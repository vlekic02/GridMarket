package com.griddynamics.gridmarket.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record DiscountCreateRequest(
    @NotEmpty(message = "Discount name can't be empty")
    String name,
    @Pattern(regexp = "PERCENTAGE|FLAT",
        message = "Discount must have a valid type (PERCENTAGE, FLAT)")
    String type,
    @PositiveOrZero(message = "Discount value can't be negative")
    @NotNull(message = "Discount must have a value")
    Double value,
    @JsonProperty("start_time")
    LocalDateTime startTime,
    @JsonProperty("end_time")
    LocalDateTime endTime
) {

}
