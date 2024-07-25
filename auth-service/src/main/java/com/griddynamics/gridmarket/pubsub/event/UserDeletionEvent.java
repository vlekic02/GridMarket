package com.griddynamics.gridmarket.pubsub.event;

public record UserDeletionEvent(String username) implements GenericEvent {

}