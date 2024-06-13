package com.griddynamics.gridmarket.interceptors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class PrometheusCounterInterceptor implements HandlerInterceptor {

  private final Map<String, Counter> requestCounters;

  public PrometheusCounterInterceptor(MeterRegistry registry) {
    this.requestCounters = Map.of(
        "/:GET", Counter.builder("http_request_counter")
            .tags("uri", "/", "method", "GET")
            .register(registry),
        "/{id}:GET", Counter.builder("http_request_counter")
            .tags("uri", "/{id}", "method", "GET")
            .register(registry),
        "/{id}/reviews:GET",
        Counter.builder("http_request_counter")
            .tags("uri", "/{id}/reviews", "method", "GET")
            .register(registry)
    );
  }

  @Override
  public void postHandle(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      ModelAndView modelAndView) {
    int status = response.getStatus();
    if (status < 200 || status >= 300) {
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
