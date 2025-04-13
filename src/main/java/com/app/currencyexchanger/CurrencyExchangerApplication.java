package com.app.currencyexchanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Currency Exchanger application.
 * <p>
 * This class bootstraps the Spring Boot application by invoking the
 * {@link SpringApplication#run(Class, String...)} method, which starts the application context and
 * all necessary configurations.
 */
@SpringBootApplication
public class CurrencyExchangerApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencyExchangerApplication.class, args);
  }

}
