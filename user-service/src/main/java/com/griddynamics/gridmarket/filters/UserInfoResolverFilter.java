package com.griddynamics.gridmarket.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.GridUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order
public class UserInfoResolverFilter extends OncePerRequestFilter {

  public static final String REQUEST_USER_CONTEXT = "userContext";
  private static final String HEADER_KEY = "grid-user";
  private final ObjectMapper objectMapper;

  public UserInfoResolverFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String jsonContext = request.getHeader(HEADER_KEY);
    if (jsonContext != null) {
      GridUserInfo gridUserInfo = objectMapper.readValue(jsonContext, GridUserInfo.class);
      request.setAttribute(REQUEST_USER_CONTEXT, gridUserInfo);
    }
    filterChain.doFilter(request, response);
  }
}
