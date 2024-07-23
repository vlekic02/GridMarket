package com.griddynamics.gridmarket.services;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.griddynamics.gridmarket.events.UserRegistrationEvent;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

  private static final String USER_REGISTRATION_TOPIC = "user-registration";

  private final PubSubTemplate pubSubTemplate;

  public PubSubService(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  public void publishUserRegistration(UserRegistrationEvent event) {
    pubSubTemplate.publish(USER_REGISTRATION_TOPIC, event);
  }

}
