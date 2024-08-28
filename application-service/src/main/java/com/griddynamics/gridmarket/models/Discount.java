package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.models.Resource;
import java.time.LocalDateTime;
import java.util.function.DoubleBinaryOperator;

public class Discount extends Resource {

  private final String name;
  private final Type type;
  private final double value;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final Resource user;

  public Discount(long id, String name, Type type, double value, LocalDateTime startTime,
      LocalDateTime endTime, long userId) {
    super(id, "discount");
    this.name = name;
    this.type = type;
    this.value = value;
    this.startTime = startTime;
    this.endTime = endTime;
    this.user = new Resource(userId, "user");
  }

  public static Discount unlimited(long id, String name, Type type, double value, long userId) {
    return new Discount(id, name, type, value, null, null, userId);
  }

  public static Discount withTimeFrame(long id, String name, Type type, double value,
      LocalDateTime startTime, LocalDateTime endTime, long userId) {
    return new Discount(id, name, type, value, startTime, endTime, userId);
  }

  public Type getDiscountType() {
    return type;
  }

  public String getName() {
    return name;
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

  public Resource getUser() {
    return user;
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