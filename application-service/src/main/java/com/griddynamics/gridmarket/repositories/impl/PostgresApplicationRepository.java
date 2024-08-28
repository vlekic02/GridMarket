package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.http.request.DiscountCreateRequest;
import com.griddynamics.gridmarket.http.request.ReviewCreateRequest;
import com.griddynamics.gridmarket.mappers.ApplicationRowMapper;
import com.griddynamics.gridmarket.mappers.DiscountRowMapper;
import com.griddynamics.gridmarket.mappers.ReviewRowMapper;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.ApplicationMetadata;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.Review;
import com.griddynamics.gridmarket.repositories.ApplicationRepository;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("!cloud")
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
            SELECT discount_id, discount.name AS discount_name,
             type, "value", start_date, end_date, grid_user, application.*,
             EXISTS(
              SELECT 1 FROM sellable_application
              WHERE application = application.application_id
             ) AS verified
            FROM application
            LEFT JOIN discount on discount.discount_id = application.discount
            """,
        new ApplicationRowMapper()
    );
  }

  @Override
  public List<Application> findAll(boolean verified, Pageable pageable) {
    return template.query(
        """
            SELECT * FROM (
              SELECT discount_id, discount.name AS discount_name,
              type, "value", start_date, end_date, grid_user, application.*,
              EXISTS(
              SELECT 1 FROM sellable_application
              WHERE application = application.application_id
              ) AS verified
              FROM application
              LEFT JOIN discount on discount.discount_id = application.discount
            ) AS tb
            WHERE tb.verified = ?
            ORDER BY tb.application_id
            LIMIT ?
            OFFSET ?
            """,
        new ApplicationRowMapper(),
        verified,
        pageable.getPageSize(),
        pageable.getOffset()
    );
  }

  @Override
  public List<Application> findBySearchKey(boolean verified, String searchKey, Pageable pageable) {
    return template.query(
        """
            SELECT * FROM (
              SELECT discount_id, discount.name AS discount_name,
              type, "value", start_date, end_date, grid_user, application.*,
              EXISTS(
              SELECT 1 FROM sellable_application
              WHERE application = application.application_id
              ) AS verified,
              (
              SELECT COUNT(review.review_id) FROM review
              WHERE review.application = application.application_id
              ) as review_count
              FROM application
              LEFT JOIN discount on discount.discount_id = application.discount
            ) AS tb
            WHERE tb.verified = ?
            AND (LOWER(tb.name) LIKE '%' || ? || '%' OR LOWER(tb.description) LIKE '%' || ? || '%')
            ORDER BY tb.review_count, tb.application_id
            LIMIT ?
            OFFSET ?
            """,
        new ApplicationRowMapper(),
        verified,
        searchKey,
        searchKey,
        pageable.getPageSize(),
        pageable.getOffset()
    );
  }

  @Override
  public Optional<Application> findById(long id) {
    Stream<Application> applicationStream = template.queryForStream(
        """
            SELECT discount_id, discount.name AS discount_name,
             type, "value", start_date, end_date, grid_user, application.*,
             EXISTS(
              SELECT 1 FROM sellable_application
              WHERE application = application.application_id
             ) AS verified
            FROM application
            LEFT JOIN discount on discount.discount_id = application.discount
            WHERE application_id = ?
            """,
        new ApplicationRowMapper(),
        id
    );
    Optional<Application> applicationOptional = applicationStream.findFirst();
    applicationStream.close();
    return applicationOptional;
  }

  @Override
  public Optional<Application> findByName(String name) {
    Stream<Application> applicationStream = template.queryForStream(
        """
            SELECT discount_id, discount.name AS discount_name,
             type, "value", start_date, end_date, grid_user, application.*,
             EXISTS(
              SELECT 1 FROM sellable_application
              WHERE application = application.application_id
             ) AS verified
            FROM application
            LEFT JOIN discount on discount.discount_id = application.discount
            WHERE application.name = ?
            """,
        new ApplicationRowMapper(),
        name
    );
    Optional<Application> applicationOptional = applicationStream.findFirst();
    applicationStream.close();
    return applicationOptional;
  }

  @Override
  public Optional<Discount> findDiscountById(long id) {
    Stream<Discount> discountStream = template.queryForStream(
        """
            SELECT discount_id, name AS discount_name, type, "value", start_date, end_date,
            grid_user
            FROM discount
            WHERE discount_id = ?
            """,
        new DiscountRowMapper(),
        id
    );
    Optional<Discount> discountOptional = discountStream.findFirst();
    discountStream.close();
    return discountOptional;
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

  @Override
  public void createReview(long applicationId, long userId, ReviewCreateRequest request) {
    template.update(
        """
            INSERT INTO review
            VALUES (DEFAULT, ?, ?, ?, ?)
            """,
        userId,
        request.message(),
        request.stars(),
        applicationId
    );
  }

  @Override
  public void verifyApplication(long id, LocalDateTime startTime, LocalDateTime endTime) {
    template.update(
        """
            INSERT INTO sellable_application VALUES (?, ?, ?)
            ON CONFLICT (application) DO UPDATE
            SET start_date = ?, end_date = ?
            """,
        id,
        startTime,
        endTime,
        startTime,
        endTime
    );
  }

  @Override
  public void removeVerification(long id) {
    template.update(
        """
            DELETE FROM sellable_application
            WHERE application = ?
            """,
        id
    );
  }

  @Override
  public void save(Application application) {
    template.update(
        """
            UPDATE application
            SET name = ?,
            description = ?,
            price = ?,
            discount = ?
            WHERE application_id = ?
            """,
        application.getName(),
        application.getDescription(),
        application.getOriginalPrice(),
        application.getDiscount() == null ? null : application.getDiscount().getId(),
        application.getId()
    );
  }

  @Override
  public boolean alreadyMadeReview(long userId, long applicationId) {
    List<Review> reviews = template.query(
        """
            SELECT *
            FROM review
            WHERE application = ? AND author = ?
            LIMIT 1
            """, new ReviewRowMapper(),
        applicationId,
        userId
    );
    return !reviews.isEmpty();
  }

  @Override
  public boolean hasApplicationOwnership(long userId, long applicationId) {
    return Boolean.TRUE.equals(template.query(
        """
            SELECT 1
            FROM ownership
            WHERE  grid_user = ? AND application = ?
            """,
        ResultSet::next,
        userId,
        applicationId
    ));
  }

  @Override
  public void deleteReviewById(long id) {
    template.update(
        """
            DELETE FROM review
            WHERE review_id = ?
            """,
        id
    );
  }

  @Override
  public Path deleteApplicationById(long id) {
    return template.query(
        """
            DELETE FROM application
            WHERE application_id = ?
            RETURNING path
            """,
        rs -> {
          if (rs.next()) {
            return Path.of(rs.getString("path"));
          } else {
            return null;
          }
        },
        id
    );
  }

  @Override
  public void deleteApplicationsByUser(long userId) {
    template.update(
        """
            DELETE FROM application
            WHERE publisher = ?
            """,
        userId
    );
  }

  @Override
  public void saveApplication(ApplicationMetadata metadata, String path) {
    template.update(
        """
            INSERT INTO application
            VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)
            """,
        metadata.request().name(),
        metadata.request().description(),
        path,
        metadata.publisherId(),
        metadata.request().price(),
        null
    );
  }

  @Override
  public void addApplicationOwnership(long userId, long applicationId) {
    template.update(
        """
            INSERT INTO ownership
            VALUES (?, ?)
            """,
        userId,
        applicationId
    );
  }

  @Override
  public void createDiscount(DiscountCreateRequest request, long userId) {
    template.update(
        """
            INSERT INTO discount
            VALUES (DEFAULT, ?, ?::discount_type, ?, ?, ?, ?)
            """,
        request.name(),
        request.type(),
        request.value(),
        request.startTime(),
        request.endTime(),
        userId
    );
  }
}
