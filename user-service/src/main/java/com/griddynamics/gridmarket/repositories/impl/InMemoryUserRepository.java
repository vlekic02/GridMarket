package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Ban;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Profile("cloud")
@Repository
public class InMemoryUserRepository implements UserRepository {

  private final List<User> users;

  public InMemoryUserRepository() {
    Role adminRole = new Role(1, "ADMIN");
    Role memberRole = new Role(2, "MEMBER");
    this.users = List.of(
        new User(1, "Ilija", "Mirkovic", "imirkovic", adminRole,
            null, 1500),
        new User(2, "Nemanja", "Mijatovic", "nmijatovic", memberRole,
            null, 150.3),
        new User(3, "Marko", "Stojkovic", "mstojkovic", memberRole,
            null, 5.5),
        new User(4, "Uros", "Rankovic", "urankovic", memberRole,
            new Ban(1, 1, LocalDateTime.now(), "fraud"), 3)
    );
  }

  @Override
  public List<User> findAll(Pageable pageable) {
    return users.stream()
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .toList();
  }

  @Override
  public Optional<User> findById(long id) {
    return users.stream().filter(user -> user.getId() == id).findFirst();
  }
}
