package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Ban;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class BanRowMapper implements RowMapper<Ban> {

  @Override
  public Ban mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("ban_id");
    if (resultSet.wasNull()) {
      return null;
    }
    long issuerId = resultSet.getLong("issuer");
    LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
    String reason = resultSet.getString("reason");
    return new Ban(id, issuerId, date, reason);
  }
}
