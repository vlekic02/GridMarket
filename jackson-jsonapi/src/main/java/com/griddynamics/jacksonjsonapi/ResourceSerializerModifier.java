package com.griddynamics.jacksonjsonapi;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;
import com.griddynamics.jacksonjsonapi.writers.VirtualAttributesPropertyWriter;
import com.griddynamics.jacksonjsonapi.writers.VirtualRelationshipPropertyWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResourceSerializerModifier extends BeanSerializerModifier {

  private static final List<String> API_SPEC_FIELDS = List.of("type", "id", "attributes",
      "relationships");

  @Override
  public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
      BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
    if (isModel(beanDesc.getBeanClass())) {
      List<BeanPropertyWriter> attributes = new ArrayList<>();
      VirtualAttributesPropertyWriter attributesPropertyWriter = null;
      List<BeanPropertyWriter> relationships = new ArrayList<>();
      VirtualRelationshipPropertyWriter relationshipPropertyWriter = null;
      Iterator<BeanPropertyWriter> iterator = beanProperties.iterator();
      while (iterator.hasNext()) {
        BeanPropertyWriter beanProperty = iterator.next();
        String propertyName = beanProperty.getName();
        if (API_SPEC_FIELDS.contains(propertyName)) {
          if ("attributes".equals(propertyName)
              && beanProperty instanceof VirtualAttributesPropertyWriter) {
            attributesPropertyWriter = (VirtualAttributesPropertyWriter) beanProperty;
          } else if ("relationships".equals(propertyName)
              && beanProperty instanceof VirtualRelationshipPropertyWriter) {
            relationshipPropertyWriter = (VirtualRelationshipPropertyWriter) beanProperty;
          }
        } else {
          if (beanProperty.getAnnotation(JsonRelation.class) != null) {
            relationships.add(beanProperty);
          } else {
            attributes.add(beanProperty);
          }
          iterator.remove();
        }
      }
      if (attributesPropertyWriter != null) {
        attributesPropertyWriter.setAttributeProperties(attributes);
      }
      if (relationshipPropertyWriter != null) {
        relationshipPropertyWriter.setRelationshipProperties(relationships);
      }
      return beanProperties;
    }
    return super.changeProperties(config, beanDesc, beanProperties);
  }

  @Override
  public List<BeanPropertyWriter> orderProperties(SerializationConfig config,
      BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
    if (isModel(beanDesc.getBeanClass())) {
      BeanPropertyWriter[] orderedList = new BeanPropertyWriter[API_SPEC_FIELDS.size()];
      for (BeanPropertyWriter property : beanProperties) {
        int index = API_SPEC_FIELDS.indexOf(property.getName());
        orderedList[index] = property;
      }

      return List.of(orderedList);
    }
    return super.orderProperties(config, beanDesc, beanProperties);
  }

  public boolean isModel(Class<?> clazz) {
    return Resource.class.isAssignableFrom(clazz);
  }
}
