package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("!cloud")
@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;

  public PostgresUserRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public List<User> findAll() {
    return template.query(
        """
            SELECT "user".*, role_id, role.name as role_name, ban.* \
            FROM "user" \
            LEFT JOIN ban on ban."user" = "user".user_id \
            INNER JOIN role on role.role_id = "user".role
            """,
        new UserRowMapper()
    );
  }

  @Override
  public Optional<User> findById(long id) {
    return template.queryForStream(
        """
            SELECT "user".*, role_id, role.name as role_name, ban.* \
            FROM "user" \
            LEFT JOIN ban on ban."user" = "user".user_id \
            INNER JOIN role on role.role_id = "user".role
            WHERE user_id = ?
            """,
        new UserRowMapper(),
        id
    ).findFirst();
  }
}
