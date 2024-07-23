package com.griddynamics.gridmarket.services;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.griddynamics.gridmarket.events.UserRegistrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

  private static final String USER_REGISTRATION_TOPIC = "user-registration";
  private static final Logger LOGGER = LoggerFactory.getLogger(PubSubService.class);

  private final PubSubTemplate pubSubTemplate;

  public PubSubService(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  public void publishUserRegistration(UserRegistrationEvent event) {
    LOGGER.debug("Sending user registration event to pub/sub ! {}", event);
    pubSubTemplate.publish(USER_REGISTRATION_TOPIC, event);
  }

}
