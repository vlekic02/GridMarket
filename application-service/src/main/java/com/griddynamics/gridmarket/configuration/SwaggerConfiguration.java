package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.RestException;
import com.griddynamics.gridmarket.models.Review;
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
        .description("Application-service api documentation");
  }

  @PostConstruct
  public void injectSchemas() {
    SpringDocUtils.getConfig().replaceWithSchema(
        Application.class,
        SwaggerUtils.generateSchemaForType(Application.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        Discount.class,
        SwaggerUtils.generateSchemaForType(Discount.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        Review.class,
        SwaggerUtils.generateSchemaForType(Review.class)
    );
    SpringDocUtils.getConfig().replaceWithSchema(
        RestException.class,
        SwaggerUtils.generateSchemaForType(RestException.class)
    );
  }
}
