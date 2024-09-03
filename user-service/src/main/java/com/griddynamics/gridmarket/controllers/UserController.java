package com.griddynamics.gridmarket.controllers;

import com.griddynamics.gridmarket.annotations.AdminAccess;
import com.griddynamics.gridmarket.http.request.ModifyUserRequest;
import com.griddynamics.gridmarket.http.response.DataResponse;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  public DataResponse<Collection<User>> getAllUsers(Pageable pageable) {
    return DataResponse.of(userService.getAllUsers(pageable));
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

  @Operation(summary = "Modifies specific user info")
  @AdminAccess
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}")
  public void modifyUser(
      @PathVariable
      @Parameter(description = "User id")
      long id,
      @RequestBody
      ModifyUserRequest request
  ) {
    userService.modifyUser(id, request);
  }

  @Operation(summary = "Deletes specific user")
  @AdminAccess
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/{id}")
  public void deleteUser(
      @PathVariable
      @Parameter(description = "User id")
      long id
  ) {
    userService.deleteUser(id);
  }

  @Operation(summary = "Get specific user balance") //TODO: Only current user and admin
  @GetMapping(value = "/{id}/balance", produces = "application/vnd.api+json")
  public DataResponse<Balance> getUserBalance(
      @PathVariable
      @Parameter(description = "User id")
      long id,
      GridUserInfo userInfo
  ) {
    return DataResponse.of(userService.getUserBalance(id, userInfo));
  }
}
