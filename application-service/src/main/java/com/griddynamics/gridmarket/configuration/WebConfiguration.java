package com.griddynamics.gridmarket.configuration;

import com.griddynamics.gridmarket.filters.AdminAccessInterceptor;
import com.griddynamics.gridmarket.interceptors.PrometheusCounterInterceptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final Map<String, Counter> prometheusCounters;

  public WebConfiguration(MeterRegistry registry) {
    prometheusCounters = Map.of(
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
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new PrometheusCounterInterceptor(prometheusCounters));
    registry.addInterceptor(new AdminAccessInterceptor());
  }

  @Override
  public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new UserInfoResolver());
  }
}
