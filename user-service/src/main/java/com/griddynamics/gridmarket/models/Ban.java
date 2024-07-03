package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;
import java.time.LocalDateTime;

public class Ban extends Resource {

  private final Resource issuer;
  private final LocalDateTime date;
  private final String reason;

  public Ban(long id, long issuerId, LocalDateTime date, String reason) {
    super(id, "ban");
    this.issuer = new Resource(issuerId, "user");
    this.date = date;
    this.reason = reason;
  }

  @JsonRelation
  public Resource getIssuer() {
    return issuer;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public String getReason() {
    return reason;
  }
}
