package com.app.currencyexchanger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI (Swagger) documentation for the API.
 * <p>
 * This class defines the API's metadata (title, description, version) and security configuration
 * for JWT authentication.
 */
@Configuration
public class OpenAPIConfig {

  /**
   * Configures the OpenAPI documentation with security schemes and API metadata.
   * <p>
   * This method sets up the security scheme for bearer token authentication (JWT) and provides
   * basic information about the API, such as title, description, and version.
   *
   * @return the configured {@link OpenAPI} object containing the API documentation settings
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("API Credentials",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")  // Optional: clarify the token format
            )
        )
        .info(new Info()
            .title("Currency Exchange and Discount API's")
            .description("API for calculating currency exchange and applying discounts")
            .version("1.0")
        );
  }
}
