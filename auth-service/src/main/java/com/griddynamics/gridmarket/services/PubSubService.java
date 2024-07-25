package com.griddynamics.gridmarket.services;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

  private static final String USER_TOPIC = "user";
  private static final Logger LOGGER = LoggerFactory.getLogger(PubSubService.class);

  private final PubSubTemplate pubSubTemplate;

  public PubSubService(PubSubTemplate pubSubTemplate) {
    this.pubSubTemplate = pubSubTemplate;
  }

  public void publishUserRegistration(UserRegistrationEvent event) {
    LOGGER.debug("Publishing user registration event to pub/sub ! {}", event);
    pubSubTemplate.publish(USER_TOPIC, event, Map.of("event", "user_registration"))
        .thenAccept(id -> LOGGER.debug("User registration event published with id {} !", id));
  }

}
