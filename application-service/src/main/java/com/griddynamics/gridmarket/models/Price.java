package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.models.Resource;

public class Price extends Resource {

  private final double price;

  public Price(long id, double price) {
    super(id, "price");
    this.price = price;
  }

  public double getPrice() {
    return price;
  }
}
