package com.app.currencyexchanger.constants;

/**
 * A utility class that holds constant values for security-related configurations.
 * <p>
 * This class contains static final strings for default usernames and passwords used for different
 * roles in the application, such as employee, affiliate, and customer. These constants can be used
 * for testing or default user credentials.
 */
public class SecurityConstant {

  private SecurityConstant() {
    // Private constructor to prevent instantiation
  }

  public static final String EMPLOYEE_USERNAME = "employee";
  public static final String EMPLOYEE_PASSWORD = "emp@pas";
  public static final String AFFILIATE_USERNAME = "affiliate";
  public static final String AFFILIATE_PASSWORD = "aft@pas";
  public static final String CUSTOMER_USERNAME = "customer";
  public static final String CUSTOMER_PASSWORD = "cus@pass";
}

