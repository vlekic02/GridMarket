package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.Ban;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import com.griddynamics.jacksonjsonapi.utils.SwaggerUtils;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

  @Bean
  public ModelResolver modelResolver(ObjectMapper objectMapper) {
    return new ModelResolver(objectMapper);
  }

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("GridMarket")
        .description("User-service api documentation");
  }

  @PostConstruct
  public void injectSchemas() {
    SpringDocUtils.getConfig().replaceWithSchema(
        Balance.class,
        SwaggerUtils.generateSchemaForType(Balance.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        Role.class,
        SwaggerUtils.generateSchemaForType(Role.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        Ban.class,
        SwaggerUtils.generateSchemaForType(Ban.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        User.class,
        SwaggerUtils.generateSchemaForType(User.class)
    );
  }
}
