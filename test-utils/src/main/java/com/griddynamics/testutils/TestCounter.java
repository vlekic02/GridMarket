package com.griddynamics.testutils;

import io.micrometer.core.instrument.Counter;

public class TestCounter implements Counter {

  private double value;

  public TestCounter() {
    value = 0;
  }

  @Override
  public void increment(double v) {
    value += v;
  }

  @Override
  public double count() {
    return value;
  }

  @Override
  public Id getId() {
    return null;
  }
}
