package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;

  public PostgresUserRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    Stream<User> userStream = template.queryForStream(
        """
            SELECT *
            FROM "user"
            WHERE username = ?
            """,
        new UserRowMapper(),
        username
    );
    Optional<User> userOptional = userStream.findFirst();
    userStream.close();
    return userOptional;
  }
}
