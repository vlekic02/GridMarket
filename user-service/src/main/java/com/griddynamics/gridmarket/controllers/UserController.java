package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

  @Operation(summary = "Get all users")
  @GetMapping(produces = "application/vnd.api+json")
  public DataResponse<Collection<User>> getAllUsers() {
    return DataResponse.of(userService.getAllUsers());
  }

  @Operation(summary = "Get specific user by id")
  @GetMapping(value = "/{id}", produces = "application/vnd.api+json")
  public DataResponse<User> getUserById(
      @PathVariable
      @Parameter(description = "User id")
      long id
  ) {
    return DataResponse.of(userService.getUserById(id));
  }

  @Operation(summary = "Get specific user balance")
  @GetMapping(value = "/{id}/balance", produces = "application/vnd.api+json")
  public DataResponse<Balance> getUserBalance(
      @PathVariable
      @Parameter(description = "User id")
      long id
  ) {
    return DataResponse.of(userService.getUserBalance(id));
  }
}
