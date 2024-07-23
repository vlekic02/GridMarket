package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConfiguration {

  @Bean
  public PubSubMessageConverter jacksonPubSubMessageConverter(ObjectMapper objectMapper) {
    return new JacksonPubSubMessageConverter(objectMapper);
  }
}
