package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;

  public PostgresUserRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public List<User> findAll(Pageable pageable) {
    return template.query(
        """
            SELECT "user".*, role_id, role.name as role_name, ban.* \
            FROM "user" \
            LEFT JOIN ban on ban."user" = "user".user_id \
            INNER JOIN role on role.role_id = "user".role \
            ORDER BY user_id \
            LIMIT ? \
            OFFSET ?
            """,
        new UserRowMapper(),
        pageable.getPageSize(),
        pageable.getOffset()
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
