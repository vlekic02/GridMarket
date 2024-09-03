package com.griddynamics.gridmarket.filters;

import com.griddynamics.gridmarket.annotations.AdminAccess;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.models.GridUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class RouteAccessInterceptor implements HandlerInterceptor {

  private static final String ADMIN_ROLE = "ADMIN";

  private final Map<Class<? extends Annotation>, Predicate<GridUserInfo>> accessMapping;

  public RouteAccessInterceptor() {
    this.accessMapping = Map.of(AdminAccess.class, user -> !ADMIN_ROLE.equals(user.role()));
  }

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler
  ) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    GridUserInfo gridUserInfo = (GridUserInfo) request.getAttribute(
        UserInfoResolverFilter.REQUEST_USER_CONTEXT);
    if (gridUserInfo == null) {
      return true;
    }
    for (var annotation : accessMapping.keySet()) {
      if (!handlerMethod.hasMethodAnnotation(annotation)) {
        continue;
      }
      Predicate<GridUserInfo> predicate = accessMapping.get(annotation);
      if (predicate.test(gridUserInfo)) {
        throw new UnauthorizedException("You don't have permission to execute this request !");
      }
    }
    return true;
  }
}

