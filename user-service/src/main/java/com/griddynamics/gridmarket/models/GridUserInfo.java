package com.griddynamics.gridmarket.models;

public record GridUserInfo(
    long id,
    String name,
    String surname,
    String username,
    String role,
    double balance
) {

}
