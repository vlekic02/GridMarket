package com.griddynamics.gridmarket.models;

import java.time.LocalDateTime;
import java.util.function.DoubleBinaryOperator;

public record Discount(
    long id,
    String name,
    Type type,
    double value,
    LocalDateTime startTime,
    LocalDateTime endTime
) {

  public Discount(long id, String name, Type type, double value) {
    this(id, name, type, value, null, null);
  }

  public double getRealPrice(double price) {
    return type.calculatePrice(value, price);
  }

  public boolean isValid() {
    LocalDateTime currentTime = LocalDateTime.now();
    if (endTime == null && startTime == null) {
      return true;
    }
    if (startTime != null && startTime.isAfter(currentTime)) {
      return false;
    }
    return endTime == null || !endTime.isBefore(currentTime);
  }

  public enum Type {
    PERCENTAGE((value, price) -> price - ((value / 100) * price)),
    FLAT((value, price) -> price - value);

    private final DoubleBinaryOperator discountOperator;

    Type(DoubleBinaryOperator discountOperator) {
      this.discountOperator = discountOperator;
    }

    public double calculatePrice(double value, double price) {
      double realPrice = discountOperator.applyAsDouble(value, price);
      return realPrice <= 0 ? 0 : realPrice;
    }
  }
}
