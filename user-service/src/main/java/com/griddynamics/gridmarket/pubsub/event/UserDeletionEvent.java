package com.griddynamics.gridmarket.pubsub.event;

public record UserDeletionEvent(long id, String username) implements GenericEvent {

}
