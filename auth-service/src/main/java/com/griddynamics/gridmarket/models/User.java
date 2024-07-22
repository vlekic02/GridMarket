package com.griddynamics.gridmarket.models;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  private final long id;
  private final long userId;
  private final String username;
  private final String password;

  public User(long id, long userId, String username, String password) {
    this.id = id;
    this.userId = userId;
    this.username = username;
    this.password = password;
  }

  public long getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

}
