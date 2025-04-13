package com.app.currencyexchanger.providers;

import static java.util.Objects.nonNull;

import com.app.currencyexchanger.client.RestClient;
import com.app.currencyexchanger.exception.InternalServerException;
import com.app.currencyexchanger.model.ExchangeRateResponse;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * A provider that integrates with the Open Exchange Rates API to get exchange rates.
 * <p>
 * This class implements the CurrencyExchangeProvider interface and fetches exchange rates by
 * calling the Open Exchange Rates API, given an amount and currency pair.
 */
@Component
public class OpenExchangeRatesProvider implements CurrencyExchangeProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenExchangeRatesProvider.class);

  @Value("${openexchangerates.api.url}")
  String apiUrl;

  @Value("${openexchangerates.app-id}")
  String appId;

  private final RestClient restClient;

  /**
   * Constructs an OpenExchangeRatesProvider with the specified RestClient.
   *
   * @param restClient the RestClient used to make API requests
   */
  public OpenExchangeRatesProvider(RestClient restClient) {
    this.restClient = restClient;
  }

  /**
   * Fetches the exchange rate between two currencies for a given amount.
   *
   * @param amount       the amount to convert
   * @param fromCurrency the currency to convert from
   * @param toCurrency   the currency to convert to
   * @return the exchange rate for the specified amount and currency pair
   * @throws InternalServerException if the API call fails or returns an invalid response
   */
  @Override
  public double getExchangeRate(BigDecimal amount, String fromCurrency, String toCurrency) {
    String url = String.format("%s/convert/%s/%s/%s?app_id=%s&prettyprint=false",
        apiUrl,
        amount.stripTrailingZeros().toPlainString(),
        fromCurrency,
        toCurrency,
        appId
    );
    LOGGER.info("Calling OpenExchangeRates API: {}", url);
    try {
      ExchangeRateResponse response = restClient.exchange(
          url,
          HttpMethod.GET,
          null,
          ExchangeRateResponse.class
      );
      if (nonNull(response)) {
        LOGGER.debug("Exchange rate conversion result: {}", response.getResponse());
        return response.getResponse();
      } else {
        throw new InternalServerException("Empty response received from OpenExchangeRates API");
      }

    } catch (Exception e) {
      throw new InternalServerException(
          "Failed to fetch exchange rate from provider: " + getProviderName(), e);
    }
  }

  /**
   * Returns the name of the exchange rate provider.
   *
   * @return the provider name, in this case "openexchangerates"
   */
  @Override
  public String getProviderName() {
    return "openexchangerates";
  }
}
