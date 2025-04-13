package com.app.currencyexchanger.service;

import java.math.BigDecimal;

/**
 * Service interface for handling currency exchange operations.
 * <p>
 * This interface defines methods for retrieving exchange rates between different currencies.
 * </p>
 */
public interface CurrencyExchangeService {

  /**
   * Retrieves the exchange rate between two currencies for a given amount.
   * <p>
   * This method fetches the exchange rate for converting a specified amount from one currency to
   * another. It can involve communication with an external API or service to obtain the rate.
   * </p>
   *
   * @param amount       the amount to be converted
   * @param fromCurrency the currency code of the source currency (e.g., USD)
   * @param toCurrency   the currency code of the target currency (e.g., EUR)
   * @return the exchange rate for converting the specified amount from the source to the target
   * currency
   */
  double getExchangeRate(
      BigDecimal amount, String fromCurrency, String toCurrency
  );

}
