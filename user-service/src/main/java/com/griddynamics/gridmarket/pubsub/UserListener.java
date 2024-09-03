package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;
import com.griddynamics.gridmarket.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserListener implements ListenerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserListener.class);

  private final UserService userService;

  public UserListener(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onRegistrationEvent(UserRegistrationEvent event) {
    LOGGER.debug("Received user registration event: {}", event);
    userService.createMember(event.name(), event.surname(), event.username());
  }
}
