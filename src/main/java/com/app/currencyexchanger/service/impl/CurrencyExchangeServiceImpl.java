package com.app.currencyexchanger.service.impl;

import static java.util.Objects.isNull;

import com.app.currencyexchanger.config.CacheConfig;
import com.app.currencyexchanger.exception.BadRequestException;
import com.app.currencyexchanger.providers.CurrencyExchangeProvider;
import com.app.currencyexchanger.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);

  @Value("${exchange.provider}")
  private String activeProvider;

  private final Map<String, CurrencyExchangeProvider> providers;

  public CurrencyExchangeServiceImpl(
      @Value("${exchange.provider}") String activeProvider,
      List<CurrencyExchangeProvider> providersList
  ) {
    this.activeProvider = activeProvider;
    this.providers = providersList.stream()
        .collect(Collectors.toMap(CurrencyExchangeProvider::getProviderName, p -> p));
    LOGGER.info("Initialized CurrencyExchangeService with provider: {}", activeProvider);
  }

  @Override
  @Cacheable(
      value = CacheConfig.EXCHANGE_RATE_CACHE,
      key = "{#fromCurrency, #toCurrency}",
      unless = "#result == null"
  )
  public double getExchangeRate(BigDecimal amount, String fromCurrency, String toCurrency) {
    LOGGER.debug("Fetching exchange rate for {} {} to {}", amount, fromCurrency, toCurrency);
    CurrencyExchangeProvider provider = providers.get(activeProvider);
    if (isNull(provider)) {
      LOGGER.error("No exchange provider found with name: {}", activeProvider);
      throw new BadRequestException("Invalid exchange provider configured: " + activeProvider);
    }
    double rate = provider.getExchangeRate(amount, fromCurrency, toCurrency);
    LOGGER.info("Exchange rate from {} to {} using provider '{}' is {}", fromCurrency, toCurrency,
        activeProvider, rate);
    return rate;
  }
}
