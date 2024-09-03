package com.griddynamics.gridmarket.pubsub.event;

public record UserRegistrationEvent(String name, String surname, String username)
    implements GenericEvent {

}
