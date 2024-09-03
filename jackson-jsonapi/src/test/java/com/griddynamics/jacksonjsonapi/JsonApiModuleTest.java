package com.griddynamics.jacksonjsonapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.ErrorResource;
import com.griddynamics.jacksonjsonapi.models.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JsonApiModuleTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JsonApiModule());
  }

  @Test
  void shouldCorrectlySerializePojo() throws JsonProcessingException {
    PojoDummy pojoDummy = new PojoDummy(1, "pojo", "TestName", 10, 5);
    String expected = """
        {
            "type": "pojo",
            "id": "1",
            "attributes": {
                "age": 10,
                "name": "TestName"
            },
            "relationships": {
                "owner": {
                    "type": "person",
                    "id": "5"
                }
            }
        }
        """.replaceAll("[\\n\\r\\s]+", "");
    String actual = objectMapper.writeValueAsString(pojoDummy);
    assertEquals(expected, actual);
  }

  @Test
  void shouldCorrectlySerializePojoIfNoRelationship() throws JsonProcessingException {
    NoRelationshipPojo pojoDummy = new NoRelationshipPojo(1,
        "noRelPojo", "Test", 5);
    String expected = """
        {
            "type": "noRelPojo",
            "id": "1",
            "attributes": {
                "age": 5,
                "name": "Test"
            }
        }
        """.replaceAll("[\\n\\r\\s]+", "");
    String actual = objectMapper.writeValueAsString(pojoDummy);
    assertEquals(expected, actual);
  }

  @Test
  void shouldCorrectlySerializePojoIfNoAttributes() throws JsonProcessingException {
    NoAttributesPojo pojoDummy = new NoAttributesPojo(1, "noAttPojo", 5);
    String expected = """
        {
            "type": "noAttPojo",
            "id": "1",
            "relationships": {
                "owner": {
                    "type": "person",
                    "id": "5"
                }
            }
        }
        """.replaceAll("[\\n\\r\\s]+", "");
    String actual = objectMapper.writeValueAsString(pojoDummy);
    assertEquals(expected, actual);
  }

  @Test
  void shouldReturnCorrectylSerializedError() throws JsonProcessingException {
    ErrorResource errorResource = ErrorResource.of("TestTitle", 500, "TestDetails");
    String expected = """
        {
          "errors": [
            {
              "title": "TestTitle",
              "status": "500",
              "detail": "TestDetails"
            }
          ]
        }
        """.replaceAll("[\\n\\r\\s]+", "");
    String actual = objectMapper.writeValueAsString(errorResource);
    assertEquals(expected, actual);
  }

  private static class PojoDummy extends Resource {

    private final String name;
    private final int age;
    private final Resource owner;

    public PojoDummy(long id, String type, String name, int age, int ownerId) {
      super(id, type);
      this.name = name;
      this.age = age;
      this.owner = new Resource(ownerId, "person");
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    @JsonRelation
    public Resource getOwner() {
      return owner;
    }
  }

  private static class NoRelationshipPojo extends Resource {

    private final String name;
    private final int age;

    public NoRelationshipPojo(long id, String type, String name, int age) {
      super(id, type);
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }
  }

  private static class NoAttributesPojo extends Resource {

    private final Resource owner;

    public NoAttributesPojo(long id, String type, int ownerId) {
      super(id, type);
      this.owner = new Resource(ownerId, "person");
    }

    @JsonRelation
    public Resource getOwner() {
      return owner;
    }
  }
}
