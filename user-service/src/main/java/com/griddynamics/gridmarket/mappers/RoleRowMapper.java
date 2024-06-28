package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class RoleRowMapper implements RowMapper<Role> {

  @Override
  public Role mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("role_id");
    String name = resultSet.getString("role_name");
    return new Role(id, name);
  }
}
