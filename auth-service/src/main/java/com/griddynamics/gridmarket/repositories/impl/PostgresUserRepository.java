package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import com.griddynamics.gridmarket.requests.UserRegistrationRequest;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;
  private final PasswordEncoder passwordEncoder;

  public PostgresUserRepository(JdbcTemplate template, PasswordEncoder passwordEncoder) {
    this.template = template;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    Stream<User> userStream = template.queryForStream(
        """
            SELECT *
            FROM grid_user
            WHERE username = ?
            """,
        new UserRowMapper(),
        username
    );
    Optional<User> userOptional = userStream.findFirst();
    userStream.close();
    return userOptional;
  }

  @Override
  public void addRegisteredUser(UserRegistrationRequest userRegistrationRequest) {
    template.update(
        """
            INSERT INTO
            grid_user VALUES (default,?,?)
            """,
        userRegistrationRequest.username(),
        passwordEncoder.encode(userRegistrationRequest.password())
    );
  }
}
