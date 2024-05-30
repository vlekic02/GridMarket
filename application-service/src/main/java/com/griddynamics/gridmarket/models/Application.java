package com.griddynamics.gridmarket.models;

public record Application(
    long id,
    String name,
    String description,
    String path,
    Discount discount,
    double originalPrice,
    long publisherId
) {

  public double getRealPrice() {
    if (discount != null && discount.isValid()) {
      return discount.getRealPrice(originalPrice);
    }
    return originalPrice;
  }
}
