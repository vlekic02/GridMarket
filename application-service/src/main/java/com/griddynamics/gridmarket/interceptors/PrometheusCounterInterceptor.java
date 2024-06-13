package com.griddynamics.gridmarket.interceptors;

import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class PrometheusCounterInterceptor implements HandlerInterceptor {

  private final Map<String, Counter> requestCounters;

  public PrometheusCounterInterceptor(Map<String, Counter> requestCounters) {
    this.requestCounters = requestCounters;
  }

  @Override
  public void postHandle(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      ModelAndView modelAndView) {
    int status = response.getStatus();
    if (status < HttpStatus.OK.value() || status >= HttpStatus.MULTIPLE_CHOICES.value()) {
      return;
    }
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return;
    }
    RequestMapping requestMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
    if (requestMapping == null) {
      return;
    }
    String[] paths = requestMapping.value();
    String path = paths.length == 0 ? "/" : paths[0];
    String key = path + ":" + requestMapping.method()[0];
    Counter counter = requestCounters.get(key);
    if (counter != null) {
      counter.increment();
    }
  }
}
