package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.griddynamics.gridmarket.pubsub.Listener;
import com.griddynamics.gridmarket.pubsub.ListenerAdapter;
import com.griddynamics.gridmarket.pubsub.SubscriptionHandler;
import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import com.griddynamics.gridmarket.pubsub.event.OrderSuccessEvent;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.services.ApplicationService;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriptionConfiguration {

  private final ListenerAdapter listenerAdapter;

  public SubscriptionConfiguration(ApplicationService applicationService) {
    this.listenerAdapter = new Listener(applicationService);
  }

  @Bean("userSubscriptionHandler")
  public SubscriptionHandler userSubscriptionHandler(PubSubTemplate template,
      ObjectMapper objectMapper) {
    Map<String, Class<? extends GenericEvent>> payloads = Map.of("user_deletion",
        UserDeletionEvent.class);
    return new SubscriptionHandler(
        template,
        objectMapper,
        listenerAdapter,
        "user-application-subscription",
        payloads
    );
  }

  @Bean("orderSubscriptionHandler")
  public SubscriptionHandler orderSubscriptionHandler(PubSubTemplate template,
      ObjectMapper objectMapper) {
    Map<String, Class<? extends GenericEvent>> payloads = Map.of("order_success",
        OrderSuccessEvent.class);
    return new SubscriptionHandler(
        template,
        objectMapper,
        listenerAdapter,
        "order-subscription",
        payloads
    );
  }
}
