package com.griddynamics.gridmarket.pubsub.event;

public record UsernameChangeEvent(String oldUsername, String newUsername) implements GenericEvent {

}
