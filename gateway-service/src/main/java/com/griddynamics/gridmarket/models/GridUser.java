package com.griddynamics.gridmarket.models;

public record GridUser(
    long id, String name,
    String surname,
    String username,
    String role,
    double balance
) {

}
