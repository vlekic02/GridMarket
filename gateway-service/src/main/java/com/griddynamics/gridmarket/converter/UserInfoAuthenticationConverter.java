package com.griddynamics.gridmarket.converter;

import com.griddynamics.gridmarket.model.GridUser;
import com.griddynamics.gridmarket.token.GridUserAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class UserInfoAuthenticationConverter implements
    Converter<Jwt, Mono<GridUserAuthenticationToken>> {

  @Override
  public Mono<GridUserAuthenticationToken> convert(@NonNull Jwt source) {
    return Mono.just(new GridUserAuthenticationToken(source, new GridUser(1, "", "", "", "", 10)));
  }
}
