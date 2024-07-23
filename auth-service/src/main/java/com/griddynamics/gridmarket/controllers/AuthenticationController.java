package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import com.griddynamics.gridmarket.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

  private final UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login-page";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register-page";
  }

  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String registerUser(UserRegistrationRequest registrationRequest) {
    userService.registerUser(registrationRequest);
    return "redirect:register?success";
  }
}
