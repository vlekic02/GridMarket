package com.griddynamics.testutils;

import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

public class TestController {

  private TestController() {
  }

  public static HandlerMethod getMappedHandlerMethod() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("testGetMappedEndpoint");
    TestController controller = new TestController();
    return new HandlerMethod(controller, method);
  }

  public static HandlerMethod getUnannotatedHandlerMethod() throws NoSuchMethodException {
    Method method = TestController.class.getMethod("testNotAnnotatedMethod");
    TestController controller = new TestController();
    return new HandlerMethod(controller, method);
  }

  @GetMapping
  public void testGetMappedEndpoint() {
  }

  public void testNotAnnotatedMethod() {
  }
}
