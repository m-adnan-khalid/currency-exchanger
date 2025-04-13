package com.app.currencyexchanger.util;

import static java.util.Objects.nonNull;

import java.util.Objects;

public class ValidationUtils {

  private static final String SPACE = " ";

  private ValidationUtils() {
  }

  public static String sanitizeForLogging(Object str) {
    return nonNull(str) ? str.toString().replaceAll("[\n\r\t]", SPACE) : null;
  }


}
