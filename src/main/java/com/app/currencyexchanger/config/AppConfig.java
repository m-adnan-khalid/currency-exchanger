package com.app.currencyexchanger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class to define application beans for the Spring context.
 * <p>
 * This class includes the configuration of a {@link RestTemplate} bean which is used for making
 * HTTP requests to external APIs or services within the application.
 */
@Configuration
public class AppConfig {

  /**
   * Creates a {@link RestTemplate} bean for making HTTP requests.
   * <p>
   * The {@link RestTemplate} is a central class in Spring's HTTP communication support, used to
   * send HTTP requests and process the responses. This bean can be injected into other components
   * for making API calls.
   *
   * @return a new instance of {@link RestTemplate}
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
