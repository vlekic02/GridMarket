package com.griddynamics.gridmarket.pubsub;

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
import com.griddynamics.gridmarket.repositories.impl.InMemorySetApplicationRepository;
import com.griddynamics.gridmarket.services.ApplicationService;
import com.griddynamics.gridmarket.services.StorageService;
import com.griddynamics.gridmarket.services.impl.FileSystemStorageService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.FileSystemUtils;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {GcpPubSubEmulatorAutoConfiguration.class,
    GcpPubSubAutoConfiguration.class, GcpContextAutoConfiguration.class})
@Testcontainers
class UserSubscriptionHandlerTest {

  static final String TEST_SUBSCRIPTION = "user-application-subscription";
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
  PubSubAdmin admin;
  UserListener userListenerSpy;
  ApplicationService applicationService;

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.pubsub.emulator-host", pubsubEmulator::getEmulatorEndpoint);
    registry.add("spring.cloud.gcp.pubsub.project-id", () -> PROJECT_ID);
  }

  @BeforeEach
  void setup() {
    admin = new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient);
    admin.createTopic(TEST_TOPIC);
    admin.createSubscription(TEST_SUBSCRIPTION, TEST_TOPIC);
    StorageService storageService = new FileSystemStorageService("testApp");
    applicationService = new ApplicationService(new InMemorySetApplicationRepository(),
        storageService,
        "");
    userListenerSpy = spy(new UserListener(applicationService));
    userSubscriptionHandler = new UserSubscriptionHandler(template, new ObjectMapper(),
        userListenerSpy);
  }

  @AfterEach
  void cleanup() throws IOException {
    FileSystemUtils.deleteRecursively(Path.of("testApp"));
  }

  @Test
  void shouldDeleteApplicationAfterEventSent() throws InterruptedException {
    PubSubMessageConverter converter = new JacksonPubSubMessageConverter(new ObjectMapper());
    template.publish(TEST_TOPIC,
        converter.toPubSubMessage(
            new UserDeletionEvent(1, "User"),
            Map.of("event", "user_deletion")
        )
    ).join();
    Thread.sleep(1000);
    verify(userListenerSpy).onUserDeleteEvent(any(UserDeletionEvent.class));
  }
}
