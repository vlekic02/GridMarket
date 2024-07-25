package com.griddynamics.gridmarket.filters;

import com.griddynamics.gridmarket.annotations.AdminAccess;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.models.GridUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminAccessInterceptor implements HandlerInterceptor {

  private static final String ADMIN_ROLE = "ADMIN";
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminAccessInterceptor.class);

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler
  ) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    if (!handlerMethod.hasMethodAnnotation(AdminAccess.class)) {
      return true;
    }
    GridUserInfo gridUserInfo = (GridUserInfo) request.getAttribute(
        UserInfoResolverFilter.REQUEST_USER_CONTEXT);
    if (gridUserInfo == null) {
      return true;
    }
    if (!ADMIN_ROLE.equals(gridUserInfo.role())) {
      throw new UnauthorizedException("You don't have permission to delete users");
    }
    return true;
  }
}

