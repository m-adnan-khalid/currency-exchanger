package com.app.currencyexchanger.service;

import java.math.BigDecimal;

public interface CurrencyExchangeService {

  double getExchangeRate(
      BigDecimal amount, String fromCurrency, String toCurrency
  );

}
