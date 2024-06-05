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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VirtualAttributesPropertyWriter extends VirtualBeanPropertyWriter {

  private List<BeanPropertyWriter> attributeProperties = Collections.emptyList();

  private VirtualAttributesPropertyWriter() {
  }

  private VirtualAttributesPropertyWriter(BeanPropertyDefinition propDef,
      Annotations contextAnnotations, JavaType declaredType) {
    super(propDef, contextAnnotations, declaredType);
  }

  @Override
  protected Object value(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
    Map<String, Object> attributes = new TreeMap<>();
    for (BeanPropertyWriter property : attributeProperties) {
      property.getMember().fixAccess(true);
      Object value = property.get(bean);
      if (value != null) {
        attributes.put(property.getName(), value);
      }
    }
    return attributes.isEmpty() ? null : attributes;
  }

  @Override
  public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass,
      BeanPropertyDefinition propDef, JavaType type) {
    return new VirtualAttributesPropertyWriter(propDef,
        declaringClass.getAnnotations(),
        type);
  }

  public void setAttributeProperties(List<BeanPropertyWriter> attributeProperties) {
    this.attributeProperties = attributeProperties;
  }
}
