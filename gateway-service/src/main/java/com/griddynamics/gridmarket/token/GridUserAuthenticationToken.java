package com.griddynamics.gridmarket.token;

import com.griddynamics.gridmarket.model.GridUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

public class GridUserAuthenticationToken extends BearerTokenAuthenticationToken {

  private final Jwt jwt;
  private final GridUser gridUser;

  public GridUserAuthenticationToken(Jwt jwt, GridUser gridUser) {
    super(jwt.getTokenValue());
    this.jwt = jwt;
    this.gridUser = gridUser;
    setAuthenticated(true);
  }

  @Override
  public Jwt getCredentials() {
    return jwt;
  }

  @Override
  public GridUser getPrincipal() {
    return gridUser;
  }
}
