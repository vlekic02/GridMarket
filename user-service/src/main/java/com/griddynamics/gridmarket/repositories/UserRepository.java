package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

  List<User> findAll(Pageable pageable);

  default List<User> findAll() {
    return findAll(PageRequest.of(0, 20));
  }

  Optional<User> findById(long id);

  Optional<User> findByUsername(String username);

  void createMember(String name, String surname, String username);

  void deleteUser(long id);

  void save(User user);

  void addBalance(long id, double amount);

  void subtractBalance(long id, double amount);
}
