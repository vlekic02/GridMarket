package com.griddynamics.gridmarket.repositories;

import com.griddynamics.gridmarket.models.Role;
import java.util.Optional;

public interface RoleRepository {

  Optional<Role> findById(long id);
}
