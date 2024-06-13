package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.ApplicationRowMapper;
import com.griddynamics.gridmarket.mappers.ReviewRowMapper;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresApplicationRepository implements ApplicationRepository {

  private final JdbcTemplate template;

  public PostgresApplicationRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public List<Application> findAll() {
    return template.query(
        """
            SELECT discount_id, discount.name as discount_name, type, "value", start_date, \
            end_date, \
            application.*
            FROM application
            LEFT JOIN discount on discount.discount_id = application.discount
            """,
        new ApplicationRowMapper()
    );
  }

  @Override
  public Optional<Application> findById(long id) {
    return template.queryForStream(
        """
             SELECT discount_id, discount.name as discount_name, type, "value", start_date, \
             end_date, \
             application.*
             FROM application
             LEFT JOIN discount on discount.discount_id = application.discount
             WHERE application_id = ?
            """,
        new ApplicationRowMapper(),
        id
    ).findFirst();
  }

  @Override
  public List<Review> findReviewsByApplication(Application application) {
    return template.query(
        """
            SELECT * FROM review
            WHERE application = ?
            """,
        new ReviewRowMapper(),
        application.getId()
    );
  }
}
