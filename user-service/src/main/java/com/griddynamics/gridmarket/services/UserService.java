package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Collection<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(id, "Specified user not found !"));
  }
}
