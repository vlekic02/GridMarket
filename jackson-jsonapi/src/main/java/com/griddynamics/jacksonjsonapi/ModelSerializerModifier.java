package com.griddynamics.jacksonjsonapi;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelSerializerModifier extends BeanSerializerModifier {

  private static final List<String> API_SPEC_FIELDS = List.of("type", "id", "attributes");

  @Override
  public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
      BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
    if (isModel(beanDesc.getBeanClass())) {
      List<BeanPropertyWriter> attributes = new ArrayList<>();
      VirtualAttributesPropertyWriter attributesPropertyWriter = null;
      Iterator<BeanPropertyWriter> iterator = beanProperties.iterator();
      while (iterator.hasNext()) {
        BeanPropertyWriter beanProperty = iterator.next();
        String propertyName = beanProperty.getName();
        if (API_SPEC_FIELDS.contains(propertyName)) {
          if ("attributes".equals(propertyName)
              && beanProperty instanceof VirtualAttributesPropertyWriter) {
            attributesPropertyWriter = (VirtualAttributesPropertyWriter) beanProperty;
          }
        } else {
          attributes.add(beanProperty);
          iterator.remove();
        }
      }
      if (attributesPropertyWriter != null) {
        attributesPropertyWriter.setAttributeProperties(attributes);
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
    return Model.class.isAssignableFrom(clazz);
  }
}
