package com.griddynamics.jacksonjsonapi.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class ErrorResource {

  private final ErrorObject[] errors;

  public ErrorResource(ErrorObject... errors) {
    this.errors = errors;
  }

  public static ErrorResource of(String title, int status, String detail) {
    return new ErrorResource(new ErrorObject(title, status, detail));
  }

  public ErrorObject[] getErrors() {
    return errors;
  }

  public record ErrorObject(
      String title,
      @JsonSerialize(using = ToStringSerializer.class) int status,
      String detail
  ) {

  }
}
