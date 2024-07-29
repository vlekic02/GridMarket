package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;
  private final Logger logger = LoggerFactory.getLogger(PostgresUserRepository.class);

  public PostgresUserRepository(JdbcTemplate template) {
    this.template = template;
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
  public void addRegisteredUser(String username, String encodedPassword) {
    template.update(
        """
            INSERT INTO
            grid_user VALUES (default,?,?)
            """,
        username,
        encodedPassword
    );
  }

  @Override
  public void deleteByUsername(String username) {
    template.update(
        """
            DELETE FROM grid_user
            WHERE username = ?
            """,
        username
    );
  }

  @Override
  public void changeUsername(String oldUsername, String newUsername) {
    template.update(
        """
            UPDATE grid_user
            SET username = ?
            WHERE username = ?
            """,
        newUsername,
        oldUsername
    );
  }
}
