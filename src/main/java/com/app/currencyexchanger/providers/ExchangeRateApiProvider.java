package com.app.currencyexchanger.providers;

import static java.util.Objects.nonNull;

import com.app.currencyexchanger.client.RestClient;
import com.app.currencyexchanger.exception.InternalServerException;
import com.app.currencyexchanger.model.ExchangeRateApiResponse;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * A provider that integrates with the ExchangeRate-API to get exchange rates.
 * <p>
 * This class implements the CurrencyExchangeProvider interface and fetches exchange rates by
 * calling the ExchangeRate-API, given an amount and currency pair.
 */
@Component
public class ExchangeRateApiProvider implements CurrencyExchangeProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateApiProvider.class);

  @Value("${exchangerateapi.api.url}")
  String apiUrl;

  @Value("${exchangerateapi.api.key}")
  String apiKey;

  private final RestClient restClient;

  public ExchangeRateApiProvider(RestClient restClient) {
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
    String url = String.format("%s/%s/pair/%s/%s/%s",
        apiUrl,
        apiKey,
        fromCurrency,
        toCurrency,
        amount.stripTrailingZeros().toPlainString()
    );

    LOGGER.info("Calling ExchangeRate-API: {}", url);
    ExchangeRateApiResponse response = restClient.exchange(
        url,
        HttpMethod.GET,
        null,
        ExchangeRateApiResponse.class
    );
    if (nonNull(response)) {
      LOGGER.debug("ExchangeRate-API result: {}", response.getConversionResult());
      return response.getConversionResult();
    } else {
      LOGGER.warn("Null response received from ExchangeRate-API");
      throw new InternalServerException("Empty response received from ExchangeRate-API");
    }
  }

  /**
   * Returns the name of the exchange rate provider.
   *
   * @return the provider name, in this case "exchangerateapi"
   */
  @Override
  public String getProviderName() {
    return "exchangerateapi";
  }
}
