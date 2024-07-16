package com.griddynamics.gridmarket.models;

public class Balance {

  private final long id;
  private final double amount;

  public Balance(long id, double amount) {
    this.id = id;
    this.amount = amount;
  }

  public long getId() {
    return id;
  }

  public double getAmount() {
    return amount;
  }
}
