package com.griddynamics.gridmarket.models.internal;

import com.griddynamics.gridmarket.models.User;

public class UserInternalDto {

  private final long id;
  private final String name;
  private final String surname;
  private final String username;
  private final String role;
  private final double balance;

  public UserInternalDto(User user) {
    this.id = user.getId();
    this.name = user.getName();
    this.surname = user.getSurname();
    this.username = user.getUsername();
    this.role = user.getRole().getName();
    this.balance = user.getBalance().getAmount();
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public String getUsername() {
    return username;
  }

  public String getRole() {
    return role;
  }

  public double getBalance() {
    return balance;
  }
}
