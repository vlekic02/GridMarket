package com.griddynamics.gridmarket.http.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VerifyRequest(
    @NotNull
    Boolean verified,
    @JsonProperty("start_date")
    LocalDateTime startDate,
    @JsonProperty("end_date")
    LocalDateTime endDate
) {

  @JsonIgnore
  @AssertTrue(message = "Start and end date are not properly populated")
  public boolean isValidRequest() {
    LocalDateTime currentTime = LocalDateTime.now();
    if (startDate != null && startDate.isBefore(currentTime)) {
      return false;
    }
    return endDate == null || !endDate.isBefore(currentTime);
  }

}
