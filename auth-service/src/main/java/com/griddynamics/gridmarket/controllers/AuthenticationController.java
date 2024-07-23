package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.events.UserRegistrationEvent;
import com.griddynamics.gridmarket.services.PubSubService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

  private final PubSubService pubSubService;

  public AuthenticationController(PubSubService pubSubService) {
    this.pubSubService = pubSubService;
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login-page";
  }

  @GetMapping("/register")
  public ResponseEntity<String> registerUser() {
    pubSubService.publishUserRegistration(new UserRegistrationEvent("test", "test", "test"));
    return ResponseEntity.ok("OK");
  }
}
