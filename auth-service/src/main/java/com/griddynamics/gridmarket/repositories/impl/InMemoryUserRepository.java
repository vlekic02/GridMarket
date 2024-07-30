package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepository {

  private final Map<String, User> usersMap;
  private long lastId;

  public InMemoryUserRepository() {
    usersMap = new HashMap<>();
    List<User> users = List.of(
        new User(
            1,
            "User",
            "$2a$12$HxWrdRqiBamt3NGyp7xoreXu2Ig7yVUbtySR1mfgrZSdYBQjOHniG"
        )
    );
    lastId = 1;
    for (User user : users) {
      usersMap.put(user.getUsername().toLowerCase(), user);
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(usersMap.get(username.toLowerCase()));
  }

  @Override
  public void addRegisteredUser(String username, String encodedPassword) {
    usersMap.put(username.toLowerCase(),
        new User(++lastId,
            username, encodedPassword));
  }

  @Override
  public void deleteByUsername(String username) {
    usersMap.remove(username.toLowerCase());
  }

  @Override
  public void changePassword(String username, String encodedPassword) {
    findByUsername(username.toLowerCase()).ifPresent(user -> {
      deleteByUsername(username);
      User newUser = new User(user.getId(), user.getUsername(), encodedPassword);
      usersMap.put(newUser.getUsername().toLowerCase(), newUser);
    });
  }
}
