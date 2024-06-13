package com.griddynamics.gridmarket.mappers;

import com.griddynamics.gridmarket.models.Review;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public class ReviewRowMapper implements RowMapper<Review> {

  @Override
  public Review mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
    long id = resultSet.getLong("review_id");
    long authorId = resultSet.getLong("author");
    String message = resultSet.getString("message");
    int stars = resultSet.getInt("stars");
    long applicationId = resultSet.getLong("application");
    return new Review(id, applicationId, authorId, message, stars);
  }
}
