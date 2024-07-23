package com.griddynamics.gridmarket.services;

import com.griddynamics.gridmarket.events.UserRegistrationEvent;
import com.griddynamics.gridmarket.exceptions.UserExistsException;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PubSubService pubSubService;

  public UserService(UserRepository userRepository, PubSubService pubSubService) {
    this.userRepository = userRepository;
    this.pubSubService = pubSubService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found !"));
  }

  public void registerUser(UserRegistrationRequest registrationRequest) {
    Optional<User> alreadyRegistered = userRepository.findByUsername(
        registrationRequest.username());
    if (alreadyRegistered.isPresent()) {
      throw new UserExistsException(registrationRequest.username());
    }
    UserRegistrationEvent event = new UserRegistrationEvent(
        registrationRequest.name(),
        registrationRequest.surname(),
        registrationRequest.username()
    );
    //pubSubService.publishUserRegistration(event);
    //userRepository.registerUser(registrationRequest);
  }
}
