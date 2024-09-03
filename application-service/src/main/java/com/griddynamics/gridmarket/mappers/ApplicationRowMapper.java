package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class ApplicationRowMapper implements RowMapper<Application> {

  private final DiscountRowMapper discountRowMapper;

  public ApplicationRowMapper() {
    this.discountRowMapper = new DiscountRowMapper();
  }

  @Override
  public Application mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    Application.Builder builder = new Application.Builder();
    long id = resultSet.getLong("application_id");
    builder.setId(id);
    String name = resultSet.getString("name");
    builder.setName(name);
    String description = resultSet.getString("description");
    builder.setDescription(description);
    String path = resultSet.getString("path");
    builder.setPath(path);
    Discount discount = discountRowMapper.mapRow(resultSet, rowNum);
    builder.setDiscount(discount);
    double price = resultSet.getDouble("price");
    builder.setOriginalPrice(price);
    long publisherId = resultSet.getLong("publisher");
    builder.setPublisher(publisherId);
    boolean verified = resultSet.getBoolean("verified");
    builder.setVerified(verified);
    return builder.build();
  }
}
