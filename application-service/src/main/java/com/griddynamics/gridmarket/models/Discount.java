package com.griddynamics.gridmarket.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

public class Discount {

  private final long id;
  private final String name;
  private final Type type;
  private final double value;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public Discount(long id, String name, Type type, double value, LocalDateTime startTime,
      LocalDateTime endTime) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.value = value;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Discount(long id, String name, Type type, double value) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.value = value;
    this.startTime = null;
    this.endTime = null;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public double getValue() {
    return value;
  }

  public double getRealPrice(double price) {
    return type.calculatePrice(value, price);
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
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

  @Override
  public int hashCode() {
    return Objects.hash(id, name, type, value, startTime, endTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Discount discount)) {
      return false;
    }
    return id == discount.id && Double.compare(value, discount.value) == 0
        && Objects.equals(name, discount.name) && type == discount.type
        && Objects.equals(startTime, discount.startTime) && Objects.equals(
        endTime, discount.endTime);
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
