package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("id");
    String username = resultSet.getString("username");
    String password = resultSet.getString("password");
    return new User(id, username, password);
  }
}
