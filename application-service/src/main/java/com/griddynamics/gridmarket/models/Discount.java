package com.griddynamics.gridmarket.models;

import java.time.LocalDateTime;
import java.util.function.DoubleBinaryOperator;

public class Discount {

  private final long id;
  private final String name;
  private final Type type;
  private final double value;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  private Discount(long id, String name, Type type, double value, LocalDateTime startTime,
      LocalDateTime endTime) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.value = value;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static Discount unlimited(long id, String name, Type type, double value) {
    return new Discount(id, name, type, value, null, null);
  }

  public static Discount withTimeFrame(long id, String name, Type type, double value,
      LocalDateTime startTime, LocalDateTime endTime) {
    return new Discount(id, name, type, value, startTime, endTime);
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

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
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