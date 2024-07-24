package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;

public interface ListenerAdapter {

  default void onEvent(GenericEvent event) {
    if (event instanceof UserRegistrationEvent userRegistrationEvent) {
      onRegistrationEvent(userRegistrationEvent);
    }
  }

  void onRegistrationEvent(UserRegistrationEvent event);
}
