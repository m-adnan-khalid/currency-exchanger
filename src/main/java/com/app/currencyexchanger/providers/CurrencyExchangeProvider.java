package com.app.currencyexchanger.providers;

import java.math.BigDecimal;

/**
 * Interface for providers that handle currency exchange rate fetching.
 * <p>
 * Any class implementing this interface should provide logic for fetching exchange rates between
 * different currencies and return the provider's name.
 */
public interface CurrencyExchangeProvider {

  /**
   * Fetches the exchange rate for a given amount and currency pair.
   *
   * @param amount       the amount to convert
   * @param fromCurrency the currency to convert from
   * @param toCurrency   the currency to convert to
   * @return the exchange rate for the specified amount and currency pair
   */
  double getExchangeRate(BigDecimal amount, String fromCurrency, String toCurrency);

  /**
   * Returns the name of the currency exchange rate provider.
   *
   * @return the name of the exchange rate provider
   */
  String getProviderName();
}
