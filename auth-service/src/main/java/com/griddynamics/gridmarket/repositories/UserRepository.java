package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.User;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findByUsername(String username);

  void addRegisteredUser(String username, String encodedPassword);

  void deleteByUsername(String username);

  void changePassword(String username, String encodedPassword);
}
