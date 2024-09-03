package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.pubsub.event.UsernameChangeEvent;

public interface ListenerAdapter {

  default void onEvent(GenericEvent event) {
    if (event instanceof UserDeletionEvent userDeletionEvent) {
      onUserDeleteEvent(userDeletionEvent);
    } else if (event instanceof UsernameChangeEvent usernameChangeEvent) {
      onUsernameChangeEvent(usernameChangeEvent);
    }
  }

  void onUserDeleteEvent(UserDeletionEvent event);

  void onUsernameChangeEvent(UsernameChangeEvent event);
}
