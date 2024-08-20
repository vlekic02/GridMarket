package com.griddynamics.gridmarket.pubsub;

import com.griddynamics.gridmarket.pubsub.event.OrderSuccessEvent;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.services.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Listener implements ListenerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

  private final ApplicationService applicationService;

  public Listener(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Override
  public void onUserDeleteEvent(UserDeletionEvent event) {
    LOGGER.debug("Received user deletion event: {}", event);
    applicationService.deleteApplicationByUser(event.id());
  }

  @Override
  public void onOrderSuccessEvent(OrderSuccessEvent event) {
    LOGGER.debug("Received order success event: {}", event);
    applicationService.handleOrderSuccess(event);
  }
}
