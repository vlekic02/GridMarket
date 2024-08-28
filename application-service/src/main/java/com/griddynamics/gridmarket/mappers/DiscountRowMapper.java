package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Discount;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class DiscountRowMapper implements RowMapper<Discount> {

  @Override
  public Discount mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long discountId = resultSet.getLong("discount_id");
    if (resultSet.wasNull()) {
      return null;
    }
    String discountName = resultSet.getString("discount_name");
    Discount.Type discountType = Discount.Type.valueOf(resultSet.getString("type"));
    double value = resultSet.getDouble("value");
    Timestamp startTimestamp = resultSet.getTimestamp("start_date");
    LocalDateTime startTime = startTimestamp == null ? null : startTimestamp.toLocalDateTime();
    Timestamp endTimestamp = resultSet.getTimestamp("end_date");
    LocalDateTime endTime = endTimestamp == null ? null : endTimestamp.toLocalDateTime();
    long userId = resultSet.getLong("grid_user");
    if (startTime == null || endTime == null) {
      return Discount.unlimited(discountId, discountName, discountType, value, userId);
    } else {
      return Discount.withTimeFrame(discountId, discountName, discountType, value, startTime,
          endTime, userId);
    }
  }
}
