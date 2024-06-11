package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class ApplicationRowMapper implements RowMapper<Application> {

  @Override
  public Application mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("application_id");
    String name = resultSet.getString("name");
    String description = resultSet.getString("description");
    String path = resultSet.getString("path");
    Discount discount = null;
    long discountId = resultSet.getLong("discount_id");
    if (!resultSet.wasNull()) {
      String discountName = resultSet.getString("discount_name");
      Discount.Type discountType = Discount.Type.valueOf(resultSet.getString("type"));
      double value = resultSet.getDouble("value");
      Timestamp startTimestamp = resultSet.getTimestamp("start_date");
      LocalDateTime startTime = startTimestamp == null ? null : startTimestamp.toLocalDateTime();
      Timestamp endTimestamp = resultSet.getTimestamp("end_date");
      LocalDateTime endTime = endTimestamp == null ? null : endTimestamp.toLocalDateTime();
      if (startTime == null || endTime == null) {
        discount = Discount.unlimited(discountId, discountName, discountType, value);
      } else {
        discount = Discount.withTimeFrame(discountId, discountName, discountType, value, startTime,
            endTime);
      }
    }
    double price = resultSet.getDouble("price");
    int publisherId = resultSet.getInt("publisher");
    return new Application(id, name, description, path, discount, price, publisherId);
  }
}
