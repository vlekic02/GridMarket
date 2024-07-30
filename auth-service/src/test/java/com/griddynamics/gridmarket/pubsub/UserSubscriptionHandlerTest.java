package com.griddynamics.gridmarket.pubsub;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.spring.autoconfigure.core.GcpContextAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubEmulatorAutoConfiguration;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.griddynamics.gridmarket.pubsub.event.UserDeletionEvent;
import com.griddynamics.gridmarket.pubsub.event.UsernameChangeEvent;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import com.griddynamics.gridmarket.services.UserService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {GcpPubSubEmulatorAutoConfiguration.class,
    GcpPubSubAutoConfiguration.class, GcpContextAutoConfiguration.class})
@Testcontainers
class UserSubscriptionHandlerTest {

  static final String TEST_SUBSCRIPTION = "user-subscription";
  static final String TEST_TOPIC = "user";
  static final String PROJECT_ID = "gridmarket-dev";
  @Container
  static final PubSubEmulatorContainer pubsubEmulator = new PubSubEmulatorContainer(
      DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:485.0.0-emulators"));
  @Autowired
  PubSubTemplate template;
  @Autowired
  TopicAdminClient topicAdminClient;
  @Autowired
  SubscriptionAdminClient subscriptionAdminClient;
  UserSubscriptionHandler userSubscriptionHandler;
  UserService userService;
  PubSubAdmin admin;
  UserListener userListenerSpy;
  PubSubMessageConverter converter;

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.pubsub.emulator-host", pubsubEmulator::getEmulatorEndpoint);
    registry.add("spring.cloud.gcp.pubsub.project-id", () -> PROJECT_ID);
  }

  @BeforeEach
  void setup() {
    converter = new JacksonPubSubMessageConverter(new ObjectMapper());
    userService = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder(), null);
    admin = new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient);
    if (admin.getTopic(TEST_TOPIC) == null) {
      admin.createTopic(TEST_TOPIC);
    }
    if (admin.getSubscription(TEST_SUBSCRIPTION) == null) {
      admin.createSubscription(TEST_SUBSCRIPTION, TEST_TOPIC);
    }
    userListenerSpy = spy(new UserListener(userService));
    userSubscriptionHandler = new UserSubscriptionHandler(template, new ObjectMapper(),
        userListenerSpy);
  }

  @Test
  void shouldDeleteUserAfterEventSent() throws InterruptedException {
    template.publish(TEST_TOPIC,
        converter.toPubSubMessage(
            new UserDeletionEvent(1, "User"),
            Map.of("event", "user_deletion")
        )
    ).join();
    Thread.sleep(1000);
    verify(userListenerSpy).onUserDeleteEvent(any(UserDeletionEvent.class));
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("User"));
  }

  @Test
  void shouldChangeUserUsernameAfterEventSent() throws InterruptedException {
    template.publish(TEST_TOPIC, converter.toPubSubMessage(
            new UsernameChangeEvent("User", "NewUsername"),
            Map.of("event", "username_change")
        )
    ).join();
    Thread.sleep(1000);
    verify(userListenerSpy).onUsernameChangeEvent(any(UsernameChangeEvent.class));
    assertDoesNotThrow(() -> userService.loadUserByUsername("NewUsername"));
  }
}
