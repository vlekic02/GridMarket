package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class User extends Resource {

  private final String name;
  private final String surname;
  private final String username;
  private final Role role;
  private final Ban ban;
  private final double balance;

  public User(long id, String name, String surname, String username, Role role, Ban ban,
      double balance) {
    super(id, "user");
    this.name = name;
    this.surname = surname;
    this.username = username;
    this.role = role;
    this.ban = ban;
    this.balance = balance;
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

  @JsonRelation
  public Role getRole() {
    return role;
  }

  @JsonRelation
  public Ban getBan() {
    return ban;
  }

  public double getBalance() {
    return balance;
  }
}
