package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Collection<User> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public User getUserById(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(id, "Specified user not found !"));
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException("Specified user not found !"));
  }

  public void createMember(String name, String surname, String username) {
    userRepository.createMember(name, surname, username);
  }

  public Balance getUserBalance(long id) {
    User user = getUserById(id);
    return user.getBalance();
  }
}
