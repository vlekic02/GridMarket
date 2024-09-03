package com.griddynamics.jacksonjsonapi.utils;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SwaggerUtilsTest {

  @Test
  void shouldReturnCorrectAttributesForSchema() {
    Schema<?> testSchema = SwaggerUtils.generateSchemaForType(TestAttribute.class);
    Map<String, Schema> properties = testSchema.getProperties();
    Map<String, Schema> attributes = properties.get("attributes").getProperties();
    assertInstanceOf(ObjectSchema.class, attributes.get("testAttribute1"));
    assertInstanceOf(StringSchema.class, attributes.get("testAttribute2"));
    assertInstanceOf(NumberSchema.class, attributes.get("testAttribute3"));
    assertInstanceOf(DateTimeSchema.class, attributes.get("testAttribute4"));
  }

  @Test
  void shouldReturnCorrectRelationshipsForSchema() {
    Schema<?> testSchema = SwaggerUtils.generateSchemaForType(TestRelation.class);
    Map<String, Schema> properties = testSchema.getProperties();
    Map<String, Schema> relationships = properties.get("relationships").getProperties();
    assertInstanceOf(ObjectSchema.class, relationships.get("testRelation"));
  }

  @Test
  void shouldIgnorePropertiesWithIgnoreAnnotation() {
    Schema<?> testSchema = SwaggerUtils.generateSchemaForType(TestExclude.class);
    Map<String, Schema> properties = testSchema.getProperties();
    Map<String, Schema> attributes = properties.get("attributes").getProperties();
    assertNotNull(attributes.get("test1"));
    assertNull(attributes.get("test2"));
  }

  private static class TestAttribute extends Resource {

    private final Resource testAttribute1;
    private final String testAttribute2;
    private final long testAttribute3;
    private final LocalDateTime testAttribute4;

    public TestAttribute(long id, String type) {
      super(id, type);
      testAttribute1 = new Resource(1, "test");
      testAttribute2 = "test2";
      testAttribute3 = 5;
      testAttribute4 = LocalDateTime.now();
    }

    public Resource getTestAttribute1() {
      return testAttribute1;
    }

    public String getTestAttribute2() {
      return testAttribute2;
    }

    public long getTestAttribute3() {
      return testAttribute3;
    }

    public LocalDateTime getTestAttribute4() {
      return testAttribute4;
    }
  }

  private static class TestRelation extends Resource {

    private final Resource testRelation;

    public TestRelation(long id, String type) {
      super(id, type);
      testRelation = new Resource(1, "test");
    }

    @JsonRelation
    public Resource getTestRelation() {
      return testRelation;
    }
  }

  private static class TestExclude extends Resource {

    private final String test1;
    private final String test2;

    public TestExclude(long id, String type) {
      super(id, type);
      this.test1 = "test1";
      this.test2 = "test2";
    }

    public String getTest1() {
      return test1;
    }

    @JsonIgnore
    public String getTest2() {
      return test2;
    }
  }
}
