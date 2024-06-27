package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  List<User> findAll();

  Optional<User> findById(long id);
}
