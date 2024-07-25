package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;

public interface ListenerAdapter {

  default void onEvent(GenericEvent event) {
    if (event instanceof UserDeletionEvent userDeletionEvent) {
      onUserDeleteEvent(userDeletionEvent);
    }
  }

  void onUserDeleteEvent(UserDeletionEvent event);
}
