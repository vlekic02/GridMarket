package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.mappers.UserRowMapper;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Profile("!cloud")
@Repository
public class PostgresUserRepository implements UserRepository {

  private final JdbcTemplate template;

  public PostgresUserRepository(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public List<User> findAll(Pageable pageable) {
    return template.query(
        """
            SELECT grid_user.*, role_id, role.name as role_name, ban.*
            FROM grid_user
            LEFT JOIN ban on ban.grid_user = grid_user.user_id
            INNER JOIN role on role.role_id = grid_user.role
            ORDER BY user_id
            LIMIT ?
            OFFSET ?
            """,
        new UserRowMapper(),
        pageable.getPageSize(),
        pageable.getOffset()
    );
  }

  @Override
  public Optional<User> findById(long id) {
    Stream<User> userStream = template.queryForStream(
        """
            SELECT grid_user.*, role_id, role.name as role_name, ban.*
            FROM grid_user
            LEFT JOIN ban on ban.grid_user = grid_user.user_id
            INNER JOIN role on role.role_id = grid_user.role
            WHERE user_id = ?
            """,
        new UserRowMapper(),
        id
    );
    Optional<User> userOptional = userStream.findFirst();
    userStream.close();
    return userOptional;
  }

  @Override
  public Optional<User> findByUsername(String username) {
    Stream<User> userStream = template.queryForStream(
        """
            SELECT grid_user.*, role_id, role.name as role_name, ban.*
            FROM grid_user
            LEFT JOIN ban on ban.grid_user = grid_user.user_id
            INNER JOIN role on role.role_id = grid_user.role
            WHERE username = ?
            """,
        new UserRowMapper(),
        username
    );
    Optional<User> userOptional = userStream.findFirst();
    userStream.close();
    return userOptional;
  }

  @Override
  public void createMember(String name, String surname, String username) {
    template.update(
        """
            INSERT INTO grid_user (name, surname, username, role, balance)
            SELECT ?, ?, ?, role_id, 0
            FROM role
            WHERE name = 'MEMBER'
            """, name, surname, username
    );
  }

  @Override
  public void deleteUser(long id) {
    template.update(
        """
            DELETE FROM grid_user
            WHERE user_id = ?
            """,
        id
    );
  }

  @Override
  public void save(User user) {
    template.update(
        """
            UPDATE grid_user
            SET name = ?,
            surname = ?,
            username = ?,
            role = ?,
            balance = ?
            WHERE user_id = ?
            """,
        user.getName(),
        user.getSurname(),
        user.getUsername(),
        user.getRole().getId(),
        user.getBalance().getAmount(),
        user.getId()
    );
  }

  @Override
  public void addBalance(long id, double amount) {
    template.update(
        """
            UPDATE grid_user
            SET balance = balance + ?
            WHERE user_id = ?
            """,
        amount,
        id
    );
  }

  @Override
  public int subtractBalance(long id, double amount) {
    return template.update(
        """
            UPDATE grid_user
            SET balance = balance - ?
            WHERE user_id = ? AND balance > ?
            """,
        amount,
        id,
        amount
    );
  }
}
