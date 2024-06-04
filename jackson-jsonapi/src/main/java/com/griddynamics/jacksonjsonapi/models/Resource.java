package com.griddynamics.jacksonjsonapi.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.griddynamics.jacksonjsonapi.writers.VirtualAttributesPropertyWriter;
import com.griddynamics.jacksonjsonapi.writers.VirtualRelationshipPropertyWriter;

@JsonAppend(props = {
    @JsonAppend.Prop(value = VirtualAttributesPropertyWriter.class, name = "attributes"),
    @JsonAppend.Prop(value = VirtualRelationshipPropertyWriter.class, name = "relationships")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
public class Resource {

  @JsonSerialize(using = ToStringSerializer.class)
  private final long id;
  private final String type;

  public Resource(long id, String type) {
    this.id = id;
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Resource must contain type ! "
          + "https://jsonapi.org/format/#document-resource-objects");
    }
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public String getType() {
    return type;
  }
}
