package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.services.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserListener implements ListenerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserListener.class);

  private final ApplicationService applicationService;

  public UserListener(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Override
  public void onUserDeleteEvent(UserDeletionEvent event) {
    LOGGER.debug("Received user deletion event: {}", event);
    applicationService.deleteApplicationByUser(event.id());
  }
}
