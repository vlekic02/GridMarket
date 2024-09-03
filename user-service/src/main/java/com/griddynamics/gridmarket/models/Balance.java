package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.models.Resource;

public class Balance extends Resource {

  private final double amount;

  public Balance(long id, double amount) {
    super(id, "balance");
    this.amount = amount;
  }

  public double getAmount() {
    return amount;
  }
}
