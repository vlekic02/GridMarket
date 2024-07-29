package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.RoleRowMapper;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.repositories.RoleRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresRoleRepository implements RoleRepository {

  private final JdbcTemplate template;

  public PostgresRoleRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public Optional<Role> findById(long id) {
    Stream<Role> roleStream = template.queryForStream(
        """
            SELECT *
            FROM role
            WHERE role_id = ?
            """,
        new RoleRowMapper("name"),
        id
    );
    Optional<Role> roleOptional = roleStream.findFirst();
    roleStream.close();
    return roleOptional;
  }
}
