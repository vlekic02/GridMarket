package com.griddynamics.gridmarket.token;

import com.griddynamics.gridmarket.model.GridUser;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class GridUserAuthenticationToken extends AbstractAuthenticationToken {

  private final Jwt jwt;
  private final GridUser gridUser;

  public GridUserAuthenticationToken(Jwt jwt, GridUser gridUser,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
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
