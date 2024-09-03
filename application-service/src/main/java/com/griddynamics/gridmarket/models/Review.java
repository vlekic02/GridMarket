package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class Review extends Resource {

  private final Resource application;
  private final Resource author;
  private final String message;
  private final int stars;

  public Review(long id, long applicationId, long authorId, String message, int stars) {
    super(id, "review");
    this.application = new Resource(applicationId, "application");
    this.author = new Resource(authorId, "user");
    this.message = message;
    this.stars = stars;
  }

  @JsonRelation
  public Resource getApplication() {
    return application;
  }

  @JsonRelation
  public Resource getAuthor() {
    return author;
  }

  public String getMessage() {
    return message;
  }

  public int getStars() {
    return stars;
  }
}
