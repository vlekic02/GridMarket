package com.griddynamics.jacksonjsonapi.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ResourceTest {

  @Test
  void shouldCorrectlyConstructResource() {
    Resource resource = new Resource(1, "type");
    assertNotNull(resource);
  }

  @ParameterizedTest
  @ValueSource(strings = {""})
  @NullSource
  void shouldThrowIfNullOrEmptyType(String type) {
    assertThrows(IllegalArgumentException.class, () -> new Resource(1, type));
  }
}
