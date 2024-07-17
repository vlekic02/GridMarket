package com.griddynamics.gridmarket.configuration;

import com.griddynamics.gridmarket.client.InternalUserServiceClient;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.models.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class OpenIdConfiguration {

  private final InternalUserServiceClient userServiceClient;

  public OpenIdConfiguration(InternalUserServiceClient userServiceClient) {
    this.userServiceClient = userServiceClient;
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return (context) -> {
      if (!OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
        return;
      }
      User user = (User) context.getPrincipal().getPrincipal();
      UserInfo userInfo = userServiceClient.getUserInfo(user.getId());
      context.getClaims().claims(claims -> claims.put("user", userInfo.getClaims()));
    };
  }

}
