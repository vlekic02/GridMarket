package com.griddynamics.gridmarket.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.griddynamics.gridmarket.exceptions.NotFoundException;
import com.griddynamics.gridmarket.exceptions.UnauthorizedException;
import com.griddynamics.gridmarket.exceptions.UnprocessableEntityException;
import com.griddynamics.gridmarket.http.request.ModifyUserRequest;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.GridUserInfo;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.repositories.impl.PostgresRoleRepository;
import com.griddynamics.gridmarket.repositories.impl.PostgresUserRepository;
import com.griddynamics.gridmarket.services.PubSubService;
import com.griddynamics.gridmarket.services.UserService;
import com.griddynamics.gridmarket.utils.GridUserBuilder;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJdbcTest
@Sql(value = "/schema.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16.3-alpine");

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private UserService userService;

  private UserController userController;

  @Mock
  private PubSubService pubSubService;

  private static Stream<ModifyUserRequest> getInvalidModifyUserRequests() {
    return Stream.of(new ModifyUserRequest(
            "editedName",
            "editedSurname",
            "editedUsername",
            2L,
            250D
        ),
        new ModifyUserRequest(
            "editedName",
            "editedSurname",
            "testUsername",
            10L,
            250D
        ));
  }

  public static Stream<GridUserInfo> getBalanceGridUsers() {
    return Stream.of(
        GridUserBuilder.memberUser().setId(1).build(),
        GridUserBuilder.adminUser().setId(30).build()
    );
  }

  @BeforeEach
  void setup() {
    userService = new UserService(new PostgresUserRepository(jdbcTemplate),
        new PostgresRoleRepository(jdbcTemplate), pubSubService);
    userController = new UserController(userService);
  }

  @AfterEach
  void cleanup() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "ban", "grid_user", "role");
  }

  @Test
  void shouldReturnEmptyDataIfNoUser() {
    assertThat(userController.getAllUsers(PageRequest.of(0, 15)).getData()).isEmpty();
  }

  @Test
  void shouldThrowIfUserDoesntExist() {
    assertThrows(NotFoundException.class, () -> userController.getUserById(1));
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 0)",
      "insert into ban values (1, 1, 1, '2024-01-08 04:05:06', 'testReason')"
  })
  void shouldReturnUserIfExist() {
    User user = userController.getUserById(1).getData();
    assertTrue(
        user.getId() == 1
            && user.getRole().getName().equals("MEMBER")
            && user.getBan().getReason().equals("testReason")
    );
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (2, 'test', 'test', 'test2', 1, 0)",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 0)",
      "insert into grid_user values (3, 'test', 'test', 'test3', 1, 0)"
  })
  void shouldReturnAllUsers() {
    Collection<User> users = userController.getAllUsers(PageRequest.of(0, 30)).getData();
    assertThat(users).hasSize(3);
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 0)",
      "insert into grid_user values (2, 'test', 'test', 'test2', 1, 0)",
      "insert into grid_user values (3, 'test', 'test', 'test3', 1, 0)",
      "insert into grid_user values (4, 'test', 'test', 'test4', 1, 0)",
      "insert into grid_user values (5, 'test', 'test', 'test5', 1, 0)"
  })
  void shouldReturnCorrectlyPaginatedResult() {
    Pageable pageable = PageRequest.of(3, 1);
    Collection<User> users = userController.getAllUsers(pageable).getData();
    assertThat(users).hasSize(1).satisfies(usersCollection -> {
      User user = usersCollection.iterator().next();
      assertEquals(4, user.getId());
    });
  }

  @ParameterizedTest
  @MethodSource("getBalanceGridUsers")
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 150.25)"
  })
  void shouldReturnCorrectBalanceForUser(GridUserInfo userInfo) {
    Balance balance = userController.getUserBalance(1, userInfo).getData();
    assertEquals(150.25, balance.getAmount());
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 150.25)"
  })
  void shouldThrowIfUnauthorizedUserRequestBalance() {
    GridUserInfo userInfo = GridUserBuilder.memberUser().setId(5).build();
    assertThrows(UnauthorizedException.class, () -> userController.getUserBalance(1, userInfo));
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')"
  })
  void shouldReturnCorrectMemberAfterCreating() {
    userService.createMember("TestName", "TestSurname", "TestUsername");
    User user = userService.getUserByUsername("TestUsername");
    assertTrue(
        "TestName".equals(user.getName())
            && "TestSurname".equals(user.getSurname())
            && "TestUsername".equals(user.getUsername())
    );
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 150.25)"
  })
  void shouldCorrectlyDeleteUser() {
    userController.deleteUser(1);
    assertThrows(NotFoundException.class, () -> userController.getUserById(1));
  }

  @Test
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into role values (2, 'ADMIN')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 150.25)"
  })
  void shouldCorrectlyModifyUser() {
    ModifyUserRequest request = new ModifyUserRequest(
        "editedName",
        "editedSurname",
        "editedUsername",
        2L,
        250D
    );
    userController.modifyUser(1, request);
    User user = userController.getUserById(1).getData();
    assertTrue(
        "editedName".equals(user.getName())
            && "editedSurname".equals(user.getSurname())
            && "editedUsername".equals(user.getUsername())
            && "ADMIN".equals(user.getRole().getName())
            && 250 == user.getBalance().getAmount()
    );
  }

  @ParameterizedTest
  @MethodSource("getInvalidModifyUserRequests")
  @Sql(statements = {
      "insert into role values (1, 'MEMBER')",
      "insert into grid_user values (1, 'test', 'test', 'test', 1, 150.25)",
      "insert into grid_user values (2, 'test', 'test', 'testUsername', 1, 150.25)"
  })
  void shouldThrowIfUsernameAlreadyExist(ModifyUserRequest request) {
    assertThrows(UnprocessableEntityException.class, () -> userController.modifyUser(1, request));
  }
}
