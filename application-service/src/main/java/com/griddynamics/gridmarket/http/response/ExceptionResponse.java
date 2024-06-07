package com.griddynamics.gridmarket.http.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class ExceptionResponse extends Resource {

  private final String title;
  @JsonSerialize(using = ToStringSerializer.class)
  private final int status;
  private final String detail;

  public ExceptionResponse(long id, String title, int status, String detail) {
    super(id, "error");
    this.title = title;
    this.status = status;
    this.detail = detail;
  }

  public String getTitle() {
    return title;
  }

  public int getStatus() {
    return status;
  }

  public String getDetail() {
    return detail;
  }
}
