package com.app.currencyexchanger.constants;

public class ApiUrl {

  private ApiUrl() {
  }

  public static final String BASE_URL = "/api";
  public static final String BILL_URL = BASE_URL + "/bill";
  public static final String CALCULATE_BILL_URL = BILL_URL + "/calculate";
  public static final String AUTH_URL = BASE_URL + "/auth";
  public static final String LOGIN_URL = AUTH_URL + "/login";

}
