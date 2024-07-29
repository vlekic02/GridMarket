package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
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
  public void onUserDeleteEvent(UserDeletionEvent event) {
    /*TODO Find a way to invalidate all existing tokens for user*/
    LOGGER.debug("Recieved user deletion event: {}", event);
    userService.deleteUser(event.username());
  }
}
