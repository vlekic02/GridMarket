package com.griddynamics.gridmarket.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import com.griddynamics.gridmarket.pubsub.event.GenericEvent;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionHandler.class);
  private final ObjectMapper objectMapper;
  private final Map<String, Class<? extends GenericEvent>> payloadTypes;
  private final ListenerAdapter listenerAdapter;

  public SubscriptionHandler(
      PubSubTemplate template,
      ObjectMapper objectMapper,
      ListenerAdapter listenerAdapter,
      String subscriptionName,
      Map<String, Class<? extends GenericEvent>> payloadTypes
  ) {
    this.objectMapper = objectMapper;
    this.payloadTypes = payloadTypes;
    this.listenerAdapter = listenerAdapter;
    template.subscribe(subscriptionName, this::handle);
  }

  private void handle(BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage) {
    PubsubMessage message = basicAcknowledgeablePubsubMessage.getPubsubMessage();
    String eventName = message.getAttributesOrThrow("event");
    Class<? extends GenericEvent> eventType = payloadTypes.get(eventName);
    if (eventType == null) {
      return;
    }
    GenericEvent event = convertMessage(message, eventType);
    listenerAdapter.onEvent(event);
    basicAcknowledgeablePubsubMessage.ack();
  }

  private GenericEvent convertMessage(PubsubMessage message, Class<? extends GenericEvent> type) {
    try {
      return objectMapper.readerFor(type).readValue(message.getData().toByteArray());
    } catch (IOException exception) {
      LOGGER.error("Failed to parse Pub/Sub message data for message with id {} !",
          message.getMessageId(), exception);
      return null;
    }
  }
}
