package com.griddynamics.gridmarket.mappers;

import java.util.Map;
import java.util.function.Function;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;

public class OidcUserInfoMapper implements
    Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

  @Override
  public OidcUserInfo apply(OidcUserInfoAuthenticationContext userInfoContext) {
    Token<OidcIdToken> idToken = userInfoContext.getAuthorization().getToken(OidcIdToken.class);
    Map<String, Object> userClaims = (Map<String, Object>) idToken.getClaims().get("user");
    return new OidcUserInfo(userClaims);
  }
}
