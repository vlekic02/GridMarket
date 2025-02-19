package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.griddynamics.jacksonjsonapi.JsonApiModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper jacksonObjectMapper() {
    return new Jackson2ObjectMapperBuilder()
        .modules(new JsonApiModule())
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .defaultViewInclusion(false)
        .failOnUnknownProperties(false)
        .build();
  }
}
