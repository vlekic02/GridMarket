package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;
import com.griddynamics.gridmarket.services.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserListener extends ListenerAdapter {

  private final UserService userService;

  public UserListener(UserService userService) {
    this.userService = userService;
  }

  @Override
  public void onRegistrationEvent(UserRegistrationEvent event) {
    userService.createMember(event.name(), event.surname(), event.username());
  }
}
