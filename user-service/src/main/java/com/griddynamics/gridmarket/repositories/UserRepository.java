package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface UserRepository {

  List<User> findAll(Pageable pageable);

  default List<User> findAll() {
    return findAll(Pageable.unpaged());
  }

  Optional<User> findById(long id);
}
