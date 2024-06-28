package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.Balance;
import com.griddynamics.gridmarket.models.Ban;
import com.griddynamics.gridmarket.models.RestException;
import com.griddynamics.gridmarket.models.Role;
import com.griddynamics.gridmarket.models.User;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
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
    SpringDocUtils.getConfig().replaceWithSchema(RestException.class, getExceptionResponseSchema());
    SpringDocUtils.getConfig().replaceWithSchema(Balance.class, getBalanceSchema());
    SpringDocUtils.getConfig().replaceWithSchema(Role.class, getRoleSchema());
    SpringDocUtils.getConfig().replaceWithSchema(Ban.class, getBanSchema());
    SpringDocUtils.getConfig().replaceWithSchema(User.class, getUserSchema());
  }

  private Schema<?> getResourceSchema(String type) {
    return new ObjectSchema()
        .addProperty("id", new StringSchema().example("0"))
        .addProperty("type", new StringSchema().example(type));
  }

  private Schema<?> getExceptionResponseSchema() {
    Schema<?> errorAttributes = new ObjectSchema()
        .addProperty("details", new StringSchema())
        .addProperty("status", new NumberSchema())
        .addProperty("title", new StringSchema());

    return getResourceSchema("error")
        .addProperty("attributes", errorAttributes);
  }

  private Schema<?> getBalanceSchema() {
    Schema<?> balanceAttribute = new ObjectSchema()
        .addProperty("amount", new NumberSchema());

    return getResourceSchema("balance")
        .addProperty("attributes", balanceAttribute);
  }

  private Schema<?> getRoleSchema() {
    Schema<?> roleAttributes = new ObjectSchema()
        .addProperty("name", new StringSchema());

    return getResourceSchema("role")
        .addProperty("attributes", roleAttributes);
  }

  private Schema<?> getBanSchema() {
    Schema<?> banAttributes = new ObjectSchema()
        .addProperty("date", new DateTimeSchema())
        .addProperty("reason", new StringSchema());

    Schema<?> banRelationship = new ObjectSchema()
        .addProperty("issuer", getResourceSchema("user"));

    return getResourceSchema("ban")
        .addProperty("attributes", banAttributes)
        .addProperty("relationships", banRelationship);
  }

  private Schema<?> getUserSchema() {
    Schema<?> userAttributes = new ObjectSchema()
        .addProperty("name", new StringSchema())
        .addProperty("surname", new StringSchema())
        .addProperty("username", new StringSchema());

    Schema<?> userRelationships = new ObjectSchema()
        .addProperty("ban", getBanSchema())
        .addProperty("role", getRoleSchema());

    return getResourceSchema("user")
        .addProperty("attributes", userAttributes)
        .addProperty("relationships", userRelationships);
  }
}
