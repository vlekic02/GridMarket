package com.griddynamics.jacksonjsonapi;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonApiModule extends SimpleModule {

  public JsonApiModule() {
    super(JsonApiModule.class.getName());
  }

  @Override
  public void setupModule(SetupContext context) {
    super.setupModule(context);
    context.addBeanSerializerModifier(new ModelSerializerModifier());
  }
}
