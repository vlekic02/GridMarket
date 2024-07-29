package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Ban;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class UserRowMapper implements RowMapper<User> {

  private final RoleRowMapper roleRowMapper;
  private final BanRowMapper banRowMapper;

  public UserRowMapper() {
    this.roleRowMapper = new RoleRowMapper("role_name");
    this.banRowMapper = new BanRowMapper();
  }

  @Override
  public User mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("user_id");
    String name = resultSet.getString("name");
    String surname = resultSet.getString("surname");
    String username = resultSet.getString("username");
    double balance = resultSet.getDouble("balance");
    Ban ban = banRowMapper.mapRow(resultSet, rowNum);
    Role role = roleRowMapper.mapRow(resultSet, rowNum);
    return new User(id, name, surname, username, role, ban, balance);
  }
}
