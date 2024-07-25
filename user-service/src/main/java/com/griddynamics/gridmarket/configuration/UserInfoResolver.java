package com.griddynamics.gridmarket.configuration;

import com.griddynamics.gridmarket.models.GridUserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserInfoResolver implements HandlerMethodArgumentResolver {

  private static final String REQUEST_USER_CONTEXT = "userContext";

  @Override
  public boolean supportsParameter(@NonNull MethodParameter parameter) {
    return parameter.getParameterType().equals(GridUserInfo.class);
  }

  @Override
  public Object resolveArgument(
      @NonNull MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory
  ) throws Exception {
    return webRequest.getAttribute(REQUEST_USER_CONTEXT, 0);
  }
}
