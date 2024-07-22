package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.GridUserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserInfoResolver implements HandlerMethodArgumentResolver {

  private static final String HEADER_KEY = "grid-user";

  private final ObjectMapper objectMapper;

  public UserInfoResolver(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

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
    String jsonContext = webRequest.getHeader(HEADER_KEY);
    return objectMapper.readValue(jsonContext, GridUserInfo.class);
  }
}
