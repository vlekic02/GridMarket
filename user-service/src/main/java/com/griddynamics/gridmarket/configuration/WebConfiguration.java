package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final ObjectMapper objectMapper;

  public WebConfiguration(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new UserInfoResolver(objectMapper));
  }
}
