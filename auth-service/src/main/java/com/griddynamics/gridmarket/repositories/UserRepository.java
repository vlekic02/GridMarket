package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findByUsername(String username);

  void addRegisteredUser(UserRegistrationRequest userRegistrationRequest);
}
