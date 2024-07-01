package com.griddynamics.gridmarket.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.griddynamics.testutils.TestController;
import com.griddynamics.testutils.TestCounter;
import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.method.HandlerMethod;

class PrometheusCounterInterceptorTest {

  private PrometheusCounterInterceptor interceptor;
  private Counter testCounter;
  private HandlerMethod handler;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setup() throws NoSuchMethodException {
    request = get("/").buildRequest(new MockServletContext());
    response = new MockHttpServletResponse();
    testCounter = new TestCounter();
    interceptor = new PrometheusCounterInterceptor(Map.of("/:GET", testCounter));
    handler = TestController.getMappedHandlerMethod();
  }

  @Test
  void shouldIncrementCounterIfEndpointHit() {
    interceptor.postHandle(request, response, handler, null);
    assertEquals(1, testCounter.count());
  }

  @Test
  void shouldNotIncrementIfNotSuccessHttpStatus() {
    response.setStatus(500);
    interceptor.postHandle(request, response, handler, null);
    assertEquals(0, testCounter.count());
  }

  @Test
  void shouldNotIncrementIfHandlerNotHandlerMethod() {
    interceptor.postHandle(request, response, "", null);
    assertEquals(0, testCounter.count());
  }

  @Test
  void shouldNotIncrementIfNoMappingAnnotation() throws NoSuchMethodException {
    handler = TestController.getUnannotatedHandlerMethod();
    interceptor.postHandle(request, response, handler, null);
    assertEquals(0, testCounter.count());
  }
}