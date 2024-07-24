package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;

public abstract class ListenerAdapter {

  public void onEvent(GenericEvent event) {
    if (event instanceof UserRegistrationEvent userRegistrationEvent) {
      onRegistrationEvent(userRegistrationEvent);
    }
  }

  public abstract void onRegistrationEvent(UserRegistrationEvent event);
}
