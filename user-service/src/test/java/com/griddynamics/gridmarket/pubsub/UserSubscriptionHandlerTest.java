package com.griddynamics.gridmarket.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.spring.autoconfigure.pubsub.GcpPubSubAutoConfiguration;
import com.google.cloud.spring.core.DefaultGcpEnvironmentProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpEnvironmentProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.griddynamics.gridmarket.pubsub.UserSubscriptionHandlerTest.PubSubEmulatorConfiguration;
import com.griddynamics.gridmarket.pubsub.event.UserRegistrationEvent;
import com.griddynamics.gridmarket.repositories.impl.InMemoryUserRepository;
import com.griddynamics.gridmarket.services.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {GcpPubSubAutoConfiguration.class,
    PubSubEmulatorConfiguration.class})
@Testcontainers
class UserSubscriptionHandlerTest {

  static final String TEST_SUBSCRIPTION = "user-subscription";
  static final String TEST_TOPIC = "user";
  static final String PROJECT_ID = "gridmarket-dev";

  @Container
  static final PubSubEmulatorContainer pubsubEmulator = new PubSubEmulatorContainer(
      DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:485.0.0-emulators"));
  UserSubscriptionHandler handler;
  PublisherFactory publisherFactory;
  PubSubAdmin admin;
  ManagedChannel channel;
  UserService userService;
  @Autowired
  DefaultSubscriberFactory subscriberFactory;
  PubSubTemplate template;

  @DynamicPropertySource
  static void emulatorProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.gcp.pubsub.emulator-host", pubsubEmulator::getEmulatorEndpoint);
  }

  @BeforeEach
  void setup() throws IOException {
    channel =
        ManagedChannelBuilder.forTarget("dns:///" + pubsubEmulator.getEmulatorEndpoint())
            .usePlaintext()
            .build();
    TransportChannelProvider channelProvider =
        FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

    TopicAdminClient topicAdminClient =
        TopicAdminClient.create(
            TopicAdminSettings.newBuilder()
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(channelProvider)
                .build());

    SubscriptionAdminClient subscriptionAdminClient =
        SubscriptionAdminClient.create(
            SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build());

    admin =
        new PubSubAdmin(() -> PROJECT_ID, topicAdminClient, subscriptionAdminClient);
    admin.createTopic(TEST_TOPIC);
    admin.createSubscription(TEST_SUBSCRIPTION, TEST_TOPIC);
    publisherFactory = (topic) -> {
      try {
        return Publisher.newBuilder(topic)
            .setChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
    subscriberFactory.setChannelProvider(channelProvider);
    subscriberFactory.setCredentialsProvider(NoCredentialsProvider.create());
    template = new PubSubTemplate(publisherFactory, subscriberFactory);
    userService = new UserService(new InMemoryUserRepository());
    handler = new UserSubscriptionHandler(template, new ObjectMapper(),
        new UserListener(userService));
    admin.close();
    channel.shutdown();
  }

  @Test
  void test() {
    PubSubMessageConverter pubSubMessageConverter = new JacksonPubSubMessageConverter(
        new ObjectMapper());
    template.publish(TEST_TOPIC,
        pubSubMessageConverter.toPubSubMessage(new UserRegistrationEvent("Test", "Test", "Test"),
            Map.of()));

  }

  @AfterEach
  void cleanup() {
    //await().until(() -> template.pullAndAck(TEST_SUBSCRIPTION, 1000, true), hasSize(0));
  }

  @TestConfiguration
  static class PubSubEmulatorConfiguration {

    @Bean
    GcpEnvironmentProvider gcpEnvironmentProvider() {
      return new DefaultGcpEnvironmentProvider();
    }

    @Bean
    CredentialsProvider googleCredentials() {
      return NoCredentialsProvider.create();
    }

    @Bean
    GcpProjectIdProvider gcpProjectIdProvider() {
      return new DefaultGcpProjectIdProvider();
    }

  }
}
