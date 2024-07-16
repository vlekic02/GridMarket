package com.griddynamics.gridmarket.models;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  private final long id;
  private final String name;
  private final String surname;
  private final String username;
  private final String password;
  private final Role role;
  private final double balance;

  public User(long id, String name, String surname, String username, String password, Role role,
      double balance) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.username = username;
    this.password = password;
    this.role = role;
    this.balance = balance;
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

  public Role getRole() {
    return role;
  }

  public double getBalance() {
    return balance;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.getName()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }
}
