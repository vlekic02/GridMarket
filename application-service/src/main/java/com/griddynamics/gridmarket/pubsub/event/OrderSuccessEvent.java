package com.griddynamics.gridmarket.pubsub.event;

public record OrderSuccessEvent(long user, long application) implements GenericEvent {

}
