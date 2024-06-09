package com.griddynamics.gridmarket.http.response;

import com.griddynamics.gridmarket.models.RestException;

public class ExceptionResponse {

  private final RestException[] errors;

  private ExceptionResponse(RestException... errors) {
    this.errors = errors;
  }

  public static ExceptionResponse of(RestException... errors) {
    return new ExceptionResponse(errors);
  }

  public RestException[] getErrors() {
    return errors;
  }
}
