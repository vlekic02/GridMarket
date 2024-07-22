package com.griddynamics.gridmarket.converter;

import com.griddynamics.gridmarket.clients.UserInfoClient;
import com.griddynamics.gridmarket.token.GridUserAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class UserInfoAuthenticationConverter implements
    Converter<Jwt, Mono<GridUserAuthenticationToken>> {

  private final UserInfoClient userInfoClient;

  public UserInfoAuthenticationConverter(UserInfoClient userInfoClient) {
    this.userInfoClient = userInfoClient;
  }

  @Override
  public Mono<GridUserAuthenticationToken> convert(@NonNull Jwt source) {
    return userInfoClient.getUserInfo(source)
        .map(user -> new GridUserAuthenticationToken(source, user));
  }
}
