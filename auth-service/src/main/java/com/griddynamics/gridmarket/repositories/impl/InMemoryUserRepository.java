package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

  private final Map<String, User> usersMap;

  public InMemoryUserRepository() {
    usersMap = new HashMap<>();
    List<User> users = List.of(
        new User(
            1,
            "User",
            "$2a$12$HxWrdRqiBamt3NGyp7xoreXu2Ig7yVUbtySR1mfgrZSdYBQjOHniG"
        )
    );
    for (User user : users) {
      usersMap.put(user.getUsername().toLowerCase(), user);
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(usersMap.get(username.toLowerCase()));
  }

  @Override
  public void registerUser(UserRegistrationRequest userRegistrationRequest) {

  }
}
