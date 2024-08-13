package com.griddynamics.gridmarket.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class Application extends Resource {

  private final String name;
  private final String description;
  private final String path;
  private final Discount discount;
  private final double originalPrice;
  private final Resource publisher;
  private final boolean verified;

  public Application(long id, String name, String description, String path, Discount discount,
      double originalPrice, long publisherId, boolean verified) {
    super(id, "application");
    this.name = name;
    this.description = description;
    this.path = path;
    this.discount = discount;
    this.originalPrice = originalPrice;
    this.publisher = new Resource(publisherId, "user");
    this.verified = verified;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @JsonIgnore
  public String getPath() {
    return path;
  }

  @JsonRelation
  public Discount getDiscount() {
    return discount;
  }

  public double getOriginalPrice() {
    return originalPrice;
  }

  @JsonRelation
  public Resource getPublisher() {
    return publisher;
  }

  public boolean isVerified() {
    return verified;
  }

  public double getRealPrice() {
    if (discount != null && discount.isValid()) {
      return discount.getRealPrice(originalPrice);
    }
    return originalPrice;
  }

  public Builder builder() {
    return new Builder(this);
  }

  public static class Builder {

    private long id;
    private String name;
    private String description;
    private String path;
    private Discount discount;
    private double originalPrice;
    private long publisherId;
    private boolean verified;
    private boolean changed;

    public Builder(Application application) {
      this.setId(application.getId())
          .setName(application.getName())
          .setDescription(application.getDescription())
          .setPath(application.getPath())
          .setDiscount(application.getDiscount())
          .setOriginalPrice(application.getOriginalPrice())
          .setPublisher(application.getPublisher().getId())
          .setVerified(application.isVerified());
      this.changed = false;
    }

    public Builder() {
      this.changed = false;
    }

    public Builder setId(long id) {
      this.id = id;
      this.changed = true;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      this.changed = true;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      this.changed = true;
      return this;
    }

    public Builder setPath(String path) {
      this.path = path;
      this.changed = true;
      return this;
    }

    public Builder setDiscount(Discount discount) {
      this.discount = discount;
      this.changed = true;
      return this;
    }

    public Builder setOriginalPrice(double originalPrice) {
      this.originalPrice = originalPrice;
      this.changed = true;
      return this;
    }

    public Builder setPublisher(long publisherId) {
      this.publisherId = publisherId;
      this.changed = true;
      return this;
    }

    public Builder setVerified(boolean verified) {
      this.verified = verified;
      this.changed = true;
      return this;
    }

    public boolean isChanged() {
      return changed;
    }

    public Application build() {
      return new Application(id, name, description, path, discount, originalPrice, publisherId,
          verified);
    }
  }
}
