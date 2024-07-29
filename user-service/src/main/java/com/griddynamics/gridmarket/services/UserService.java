package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.http.ModifyUserRequest;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PubSubService pubSubService;

  public UserService(UserRepository userRepository, PubSubService pubSubService) {
    this.userRepository = userRepository;
    this.pubSubService = pubSubService;
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

  public void deleteUser(long id) {
    Optional<User> userOptional = userRepository.findById(id);
    userRepository.deleteUser(id);
    userOptional.ifPresent(user -> {
      UserDeletionEvent event = new UserDeletionEvent(user.getId(), user.getUsername());
      pubSubService.publishUserDeletion(event);
    });
  }

  public void modifyUser(long id, ModifyUserRequest request) {
    
  }
}
