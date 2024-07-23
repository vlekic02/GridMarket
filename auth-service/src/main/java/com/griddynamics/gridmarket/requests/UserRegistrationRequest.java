package com.griddynamics.gridmarket.requests;

public record UserRegistrationRequest(String name, String surname, String username,
                                      String password) {

}
