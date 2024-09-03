package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class RoleRowMapper implements RowMapper<Role> {

  private final String nameColumn;

  public RoleRowMapper(String nameColumn) {
    this.nameColumn = nameColumn;
  }

  @Override
  public Role mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("role_id");
    String name = resultSet.getString(nameColumn);
    return new Role(id, name);
  }
}
