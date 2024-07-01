package com.griddynamics.jacksonjsonapi.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerUtils {

  private static final Set<Class<?>> numericTypes = Set.of(
      short.class,
      Short.class,
      int.class,
      Integer.class,
      long.class,
      Long.class,
      float.class,
      Float.class,
      double.class,
      Double.class
  );
  private static final Logger logger = LoggerFactory.getLogger(SwaggerUtils.class);

  private SwaggerUtils() {
  }

  public static Schema<?> generateSchemaForType(Class<? extends Resource> clazz) {
    Schema<?> resourceSchema = new ObjectSchema()
        .addProperty("id", new StringSchema())
        .addProperty("type", new StringSchema());
    if (clazz == Resource.class) {
      return resourceSchema;
    }
    BeanInfo beanInfo;
    try {
      beanInfo = Introspector.getBeanInfo(clazz, Resource.class);
    } catch (IntrospectionException e) {
      logger.error("Failed to read bean info for {}", clazz.getSimpleName(), e);
      return null;
    }
    Schema<?> attributesSchema = new ObjectSchema();
    Schema<?> relationshipsSchema = new ObjectSchema();
    boolean haveAttribute = false;
    boolean haveRelationship = false;
    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
      Method getterMethod = propertyDescriptor.getReadMethod();
      if (getterMethod.isAnnotationPresent(JsonIgnore.class)) {
        continue;
      }
      Class<?> returnType = getterMethod.getReturnType();

      Schema<?> returnSchema;
      if (numericTypes.contains(returnType)) {
        returnSchema = new NumberSchema();
      } else if (Resource.class.isAssignableFrom(returnType)) {
        returnSchema = generateSchemaForType(returnType.asSubclass(Resource.class));
      } else if (LocalDateTime.class.isAssignableFrom(returnType)) {
        returnSchema = new DateTimeSchema();
      } else {
        returnSchema = new StringSchema();
      }

      if (getterMethod.isAnnotationPresent(JsonRelation.class)) {
        relationshipsSchema.addProperty(propertyDescriptor.getName(), returnSchema);
        haveRelationship = true;
      } else {
        attributesSchema.addProperty(propertyDescriptor.getName(), returnSchema);
        haveAttribute = true;
      }
    }

    if (haveAttribute) {
      resourceSchema.addProperty("attributes", attributesSchema);
    }
    if (haveRelationship) {
      resourceSchema.addProperty("relationships", relationshipsSchema);
    }
    return resourceSchema;
  }
}
