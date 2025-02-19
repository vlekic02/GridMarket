package com.griddynamics.gridmarket.repositories.impl;

import com.griddynamics.gridmarket.models.Ban;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Profile("cloud")
@Repository
public class InMemoryUserRepository implements UserRepository {

  private final List<User> users;
  private long lastId;

  public InMemoryUserRepository() {
    Role adminRole = new Role(1, "ADMIN");
    Role memberRole = new Role(2, "MEMBER");
    this.users = new ArrayList<>(Arrays.asList(
        new User(1, "Ilija", "Mirkovic", "imirkovic", adminRole,
            null, 1500),
        new User(2, "Nemanja", "Mijatovic", "nmijatovic", memberRole,
            null, 150.3),
        new User(3, "Marko", "Stojkovic", "mstojkovic", memberRole,
            null, 5.5),
        new User(4, "Uros", "Rankovic", "urankovic", memberRole,
            new Ban(1, 1, LocalDateTime.now(), "fraud"), 3)
    ));
    lastId = 4;
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

  @Override
  public Optional<User> findByUsername(String username) {
    return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
  }

  @Override
  public void createMember(String name, String surname, String username) {
    users.add(
        new User(++lastId, name, surname, username,
            new Role(1, "MEMBER"), null, 0))
    ;
  }

  @Override
  public void deleteUser(long id) {
    users.removeIf(user -> user.getId() == id);
  }

  @Override
  public void save(User user) {
    deleteUser(user.getId());
    users.add(user);
  }

  @Override
  public void addBalance(long id, double amount) {
    findById(id).ifPresent(user -> {
      User newUser = user.builder()
          .setBalance(user.getBalance().getAmount() + amount)
          .build();
      deleteUser(id);
      users.add(newUser);
    });
  }

  @Override
  public int subtractBalance(long id, double amount) {
    return findById(id)
        .filter(user -> user.getBalance().getAmount() >= amount)
        .map(user -> {
          User newUser = user.builder()
              .setBalance(user.getBalance().getAmount() - amount)
              .build();
          deleteUser(id);
          users.add(newUser);
          return 1;
        })
        .orElse(0);
  }
}
