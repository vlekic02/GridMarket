package com.griddynamics.gridmarket.models;

import java.util.Objects;

public class Application {

  private final long id;
  private final String name;
  private final String description;
  private final String path;
  private final Discount discount;
  private final double originalPrice;
  private final long publisherId;

  public Application(long id, String name, String description, String path, Discount discount,
      double originalPrice, int publisherId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.path = path;
    this.discount = discount;
    this.originalPrice = originalPrice;
    this.publisherId = publisherId;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Discount getDiscount() {
    return discount;
  }

  public double getOriginalPrice() {
    return originalPrice;
  }

  public double getRealPrice() {
    if (discount != null && discount.isValid()) {
      return discount.getRealPrice(originalPrice);
    }
    return originalPrice;
  }

  public long getPublisher() {
    return publisherId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, path, discount, originalPrice, publisherId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Application that)) {
      return false;
    }
    return id == that.id && Double.compare(originalPrice, that.originalPrice) == 0
        && publisherId == that.publisherId && Objects.equals(name, that.name)
        && Objects.equals(description, that.description) && Objects.equals(path,
        that.path) && Objects.equals(discount, that.discount);
  }
}
