package com.griddynamics.gridmarket.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.griddynamics.jacksonjsonapi.annotations.JsonRelation;
import com.griddynamics.jacksonjsonapi.models.Resource;

public class User extends Resource {

  private final String name;
  private final String surname;
  private final String username;
  private final Role role;
  private final Ban ban;
  private final Balance balance;

  public User(long id, String name, String surname, String username, Role role, Ban ban,
      double balance) {
    super(id, "user");
    this.name = name;
    this.surname = surname;
    this.username = username;
    this.role = role;
    this.ban = ban;
    this.balance = new Balance(id, balance);
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

  @JsonIgnore
  public Balance getBalance() {
    return balance;
  }

  public Builder builder() {
    return new Builder(this);
  }

  public static class Builder {

    private long id;
    private String name;
    private String surname;
    private String username;
    private Role role;
    private Ban ban;
    private double balance;

    private Builder(User user) {
      this.id = user.getId();
      this.name = user.getName();
      this.surname = user.getSurname();
      this.username = user.getUsername();
      this.role = user.getRole();
      this.ban = user.getBan();
      this.balance = user.getBalance().getAmount();
    }

    public Builder setId(long id) {
      this.id = id;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setSurname(String surname) {
      this.surname = surname;
      return this;
    }

    public Builder setUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder setRole(Role role) {
      this.role = role;
      return this;
    }

    public Builder setBan(Ban ban) {
      this.ban = ban;
      return this;
    }

    public Builder setBalance(double balance) {
      this.balance = balance;
      return this;
    }

    public User build() {
      return new User(id, name, surname, username, role, ban, balance);
    }
  }
}
