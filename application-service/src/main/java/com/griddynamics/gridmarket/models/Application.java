package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class Application extends Resource {

  private final String name;
  private final String description;
  private final String path;
  private final Discount discount;
  private final double originalPrice;
  private final Resource publisher;

  public Application(long id, String name, String description, String path, Discount discount,
      double originalPrice, long publisherId) {
    super(id, "application");
    this.name = name;
    this.description = description;
    this.path = path;
    this.discount = discount;
    this.originalPrice = originalPrice;
    this.publisher = new Resource(publisherId, "user");
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

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

  public double getRealPrice() {
    if (discount != null && discount.isValid()) {
      return discount.getRealPrice(originalPrice);
    }
    return originalPrice;
  }
}
