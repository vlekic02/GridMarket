package com.griddynamics.gridmarket.services;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

  private final PubSubTemplate pubSubTemplate;

  public PubSubService(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

}
