package com.app.currencyexchanger.providers;

import java.math.BigDecimal;

public interface CurrencyExchangeProvider {

  double getExchangeRate(BigDecimal amount, String fromCurrency, String toCurrency);

  String getProviderName();

}
