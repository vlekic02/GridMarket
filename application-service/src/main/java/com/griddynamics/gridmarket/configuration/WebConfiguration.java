package com.griddynamics.gridmarket.configuration;

import com.griddynamics.gridmarket.interceptors.PrometheusCounterInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  private final MeterRegistry meterRegistry;

  public WebConfiguration(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new PrometheusCounterInterceptor(meterRegistry));
  }
}
