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

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public UserInternalDto getUserById(@PathVariable long id) {
    return new UserInternalDto(userService.getUserById(id));
  }
}
