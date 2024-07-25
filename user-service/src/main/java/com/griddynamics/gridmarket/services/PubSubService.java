package com.griddynamics.gridmarket.services;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PubSubService {

  private static final String USER_TOPIC = "user";
  private static final Logger LOGGER = LoggerFactory.getLogger(PubSubService.class);

  private final PubSubTemplate template;

  public PubSubService(PubSubTemplate template) {
    this.template = template;
  }

  public void publishUserDeletion(UserDeletionEvent event) {
    LOGGER.debug("Publishing user deletion event to pub/sub ! {}", event);
    template.publish(USER_TOPIC, event, Map.of("event", "user_deletion"))
        .thenAccept(id -> LOGGER.debug("User deletion event published with id {} !", id));
  }
}
