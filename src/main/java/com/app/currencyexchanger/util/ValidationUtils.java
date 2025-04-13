package com.app.currencyexchanger.util;

import static java.util.Objects.nonNull;

/**
 * Utility class for input validation and sanitization logic.
 * <p>
 * Provides methods to safely sanitize input data, particularly for logging.
 */
public class ValidationUtils {

  private static final String SPACE = " ";

  private ValidationUtils() {
    // Private constructor to prevent instantiation of this utility class.
  }

  public static String sanitizeForLogging(Object str) {
    return nonNull(str) ? str.toString().replaceAll("[\n\r\t]", SPACE) : null;
  }

}
