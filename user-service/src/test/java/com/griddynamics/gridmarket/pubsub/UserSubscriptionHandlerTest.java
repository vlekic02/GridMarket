package com.griddynamics.gridmarket.pubsub;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import com.griddynamics.gridmarket.services.UserService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.pubsub.emulator-host", pubsubEmulator::getEmulatorEndpoint);
    registry.add("spring.cloud.gcp.pubsub.project-id", () -> PROJECT_ID);
  }

  @BeforeEach
  void setup() {
    userService = new UserService(new InMemoryUserRepository());
    admin = new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient);
    admin.createTopic(TEST_TOPIC);
    admin.createSubscription(TEST_SUBSCRIPTION, TEST_TOPIC);
    userSubscriptionHandler = new UserSubscriptionHandler(template, new ObjectMapper(),
        new UserListener(userService));
  }

  @Test
  void shouldAddUserAfterEventSent() throws InterruptedException {
    PubSubMessageConverter converter = new JacksonPubSubMessageConverter(new ObjectMapper());
    template.publish(TEST_TOPIC,
        converter.toPubSubMessage(
            new UserRegistrationEvent("TestName", "TestSurname", "TestUsername"),
            Map.of("event", "user_registration")
        )
    ).join();
    Thread.sleep(1000);
    User user = userService.getUserByUsername("TestUsername");
    assertTrue(
        "TestName".equals(user.getName())
            && "TestSurname".equals(user.getSurname())
            && "TestUsername".equals(user.getUsername())
    );
  }
}
