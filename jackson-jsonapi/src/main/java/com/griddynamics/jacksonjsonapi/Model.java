package com.griddynamics.jacksonjsonapi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@JsonAppend(props = {
    @JsonAppend.Prop(value = VirtualAttributesPropertyWriter.class, name = "attributes")
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true)
public abstract class Model {

  @JsonSerialize(using = ToStringSerializer.class)
  private final long id;
  private final String type;

  public Model(long id, String type) {
    this.id = id;
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Model must contain type ! "
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
