package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;
import java.time.LocalDateTime;

public class Ban extends Resource {

  private final User issuer;
  private final LocalDateTime date;
  private final String reason;

  public Ban(long id, User issuer, LocalDateTime date, String reason) {
    super(id, "ban");
    this.issuer = issuer;
    this.date = date;
    this.reason = reason;
  }

  @JsonRelation
  public User getIssuer() {
    return issuer;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getReason() {
    return reason;
  }
}
