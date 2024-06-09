package com.griddynamics.gridmarket.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.griddynamics.gridmarket.models.Application;
import com.griddynamics.gridmarket.models.Discount;
import com.griddynamics.gridmarket.models.RestException;
import com.griddynamics.gridmarket.models.Review;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.BooleanSchema;
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
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("GridMarket")
        .description("E-commerce for applications");
  }

  @PostConstruct
  public void injectSchemas() {
    SpringDocUtils.getConfig().replaceWithSchema(Application.class, getApplicationSchema());
    SpringDocUtils.getConfig().replaceWithSchema(Discount.class, getDiscountSchema());
    SpringDocUtils.getConfig().replaceWithSchema(Review.class, getReviewSchema());
    SpringDocUtils.getConfig()
        .replaceWithSchema(RestException.class, getExceptionResponseSchema());
  }

  private Schema<?> getResourceSchema(String type) {
    return new ObjectSchema()
        .addProperty("id", new StringSchema().example("0"))
        .addProperty("type", new StringSchema().example(type));
  }

  private Schema<?> getDiscountSchema() {
    Schema<?> discountAttributes = new ObjectSchema()
        .addProperty("discount_type",
            new StringSchema()
                .addEnumItem("PERCENTAGE")
                .addEnumItem("FLAT")
        )
        .addProperty("name", new StringSchema())
        .addProperty("valid", new BooleanSchema())
        .addProperty("value", new NumberSchema());
    return getResourceSchema("discount")
        .addProperty("attributes", discountAttributes);
  }

  private Schema<?> getApplicationSchema() {
    Schema<?> applicationAttributes = new ObjectSchema()
        .addProperty("name", new StringSchema())
        .addProperty("original_price", new NumberSchema())
        .addProperty("real_price", new NumberSchema())
        .addProperty("path", new StringSchema());

    Schema<?> applicationRelationships = new ObjectSchema()
        .addProperty("discount", getDiscountSchema())
        .addProperty("publisher", getResourceSchema("user"));

    return getResourceSchema("application")
        .addProperty("attributes", applicationAttributes)
        .addProperty("relationships", applicationRelationships);
  }

  private Schema<?> getReviewSchema() {
    Schema<?> reviewAttributes = new ObjectSchema()
        .addProperty("message", new StringSchema())
        .addProperty("stars", new NumberSchema());

    Schema<?> reviewRelationships = new ObjectSchema()
        .addProperty("application", getResourceSchema("application"))
        .addProperty("author", getResourceSchema("user"));

    return getResourceSchema("review")
        .addProperty("attributes", reviewAttributes)
        .addProperty("relationships", reviewRelationships);
  }

  private Schema<?> getExceptionResponseSchema() {
    Schema<?> errorAttributes = new ObjectSchema()
        .addProperty("details", new StringSchema())
        .addProperty("status", new NumberSchema())
        .addProperty("title", new StringSchema());

    return getResourceSchema("error")
        .addProperty("attributes", errorAttributes);
  }
}
