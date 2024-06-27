package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.services.UserService;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/users")
@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(produces = "application/vnd.api+json")
  public DataResponse<Collection<User>> getAllUsers() {
    return DataResponse.of(userService.getAllUsers());
  }

  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  public DataResponse<User> getUserById(@PathVariable long id) {
    return DataResponse.of(userService.getUserById(id));
  }
}
