package com.griddynamics.gridmarket.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

public class Review {

  private final long id;
  private final long applicationId;
  private final long authorId;
  private final String message;
  private final int stars;

  public Review(long id, long applicationId, long authorId, String message, int stars) {
    this.id = id;
    this.applicationId = applicationId;
    this.authorId = authorId;
    this.message = message;
    this.stars = stars;
  }

  public Review(long id, long applicationId, long authorId, int stars) {
    this.id = id;
    this.applicationId = applicationId;
    this.authorId = authorId;
    this.message = null;
    this.stars = stars;
  }

  public long getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public int getStars() {
    return stars;
  }

  public long getAuthor() {
    return authorId;
  }

  @JsonIgnore
  public long getApplicationId() {
    return applicationId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, applicationId, authorId, message, stars);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Review review)) {
      return false;
    }
    return id == review.id && applicationId == review.applicationId
        && authorId == review.authorId && stars == review.stars && Objects.equals(
        message, review.message);
  }
}
