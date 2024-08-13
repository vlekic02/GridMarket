package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.exceptions.UnprocessableEntityException;
import com.griddynamics.gridmarket.http.request.ModifyUserRequest;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.pubsub.event.UsernameChangeEvent;
import com.griddynamics.gridmarket.repositories.RoleRepository;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private static final String ADMIN_ROLE = "ADMIN";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PubSubService pubSubService;

  public UserService(UserRepository userRepository, RoleRepository roleRepository,
      PubSubService pubSubService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
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

  public Balance getUserBalance(long id, GridUserInfo userInfo) {
    if (userInfo.id() != id && !ADMIN_ROLE.equals(userInfo.role())) {
      throw new UnauthorizedException("You can't view this user balance");
    }
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
    User user = getUserById(id);
    User.Builder userBuilder = user.builder();
    boolean usernameChanged = false;
    if (request.name() != null) {
      userBuilder.setName(request.name());
    }
    if (request.surname() != null) {
      userBuilder.setSurname(request.surname());
    }
    if (request.username() != null) {
      Optional<User> userOptional = userRepository.findByUsername(request.username());
      if (userOptional.isPresent()) {
        throw new UnprocessableEntityException("Specified username already exists !");
      }
      userBuilder.setUsername(request.username());
      usernameChanged = true;
    }
    if (request.roleId() != null) {
      Optional<Role> roleOptional = roleRepository.findById(request.roleId());
      Role role = roleOptional.orElseThrow(
          () -> new UnprocessableEntityException("Specified role is not found"));
      userBuilder.setRole(role);
    }
    if (request.balance() != null) {
      userBuilder.setBalance(request.balance());
    }
    userRepository.save(userBuilder.build());
    if (usernameChanged) {
      UsernameChangeEvent event = new UsernameChangeEvent(user.getUsername(), request.username());
      pubSubService.publishUsernameChange(event);
    }
  }
}
