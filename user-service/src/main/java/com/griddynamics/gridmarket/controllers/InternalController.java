package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.models.internal.UserInternalDto;
import com.griddynamics.gridmarket.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/internal/users")
@RestController
public class InternalController {

  private final UserService userService;

  public InternalController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserInternalDto getUserByUsername(@PathVariable String username) {
    return new UserInternalDto(userService.getUserByUsername(username));
  }
}
