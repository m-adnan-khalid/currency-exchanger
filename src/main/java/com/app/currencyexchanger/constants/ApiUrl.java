package com.app.currencyexchanger.constants;

/**
 * A utility class that holds all the API endpoint URLs for the Currency Exchanger application.
 * <p>
 * This class contains static final strings that define the base URL and various endpoint URLs used
 * for API interactions. These URLs are used throughout the application to route API requests.
 */
public class ApiUrl {

  private ApiUrl() {
    // Private constructor to prevent instantiation
  }

  public static final String BASE_URL = "/api";
  public static final String BILL_URL = BASE_URL + "/bill";
  public static final String CALCULATE_BILL_URL = BILL_URL + "/calculate";
  public static final String AUTH_URL = BASE_URL + "/auth";
  public static final String LOGIN_URL = AUTH_URL + "/login";

}
