package com.griddynamics.jacksonjsonapi.writers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VirtualRelationshipPropertyWriter extends VirtualBeanPropertyWriter {

  private List<BeanPropertyWriter> relationshipProperties;

  private VirtualRelationshipPropertyWriter() {
  }

  private VirtualRelationshipPropertyWriter(BeanPropertyDefinition propDef,
      Annotations contextAnnotations, JavaType declaredType) {
    super(propDef, contextAnnotations, declaredType);
  }

  @Override
  protected Object value(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
    Map<String, Object> relationships = new TreeMap<>();
    for (BeanPropertyWriter property : relationshipProperties) {
      property.getMember().fixAccess(true);
      Object value = property.get(bean);
      if (value != null) {
        Map<String, Object> data = Map.of("data", value);
        relationships.put(property.getName(), data);
      }
    }
    return relationships.isEmpty() ? null : relationships;
  }

  @Override
  public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass,
      BeanPropertyDefinition propDef, JavaType type) {
    return new VirtualRelationshipPropertyWriter(propDef,
        declaringClass.getAnnotations(),
        type);
  }

  public void setRelationshipProperties(List<BeanPropertyWriter> relationshipProperties) {
    this.relationshipProperties = relationshipProperties;
  }
}
