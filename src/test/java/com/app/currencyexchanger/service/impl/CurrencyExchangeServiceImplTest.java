package com.app.currencyexchanger.service.impl;

import com.app.currencyexchanger.exception.BadRequestException;
import com.app.currencyexchanger.providers.CurrencyExchangeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyExchangeServiceImplTest {

  private CurrencyExchangeProvider mockProvider;
  private CurrencyExchangeServiceImpl currencyExchangeService;

  private static final String VALID_PROVIDER_NAME = "openexchangerates";
  private static final BigDecimal AMOUNT = new BigDecimal("100");
  private static final String FROM = "USD";
  private static final String TO = "EUR";

  @BeforeEach
  void setup() {
    mockProvider = mock(CurrencyExchangeProvider.class);
    when(mockProvider.getProviderName()).thenReturn(VALID_PROVIDER_NAME);

    currencyExchangeService = new CurrencyExchangeServiceImpl(
        VALID_PROVIDER_NAME,
        List.of(mockProvider)
    );
  }

  @Test
  void testWithValidProviderAndShouldReturnExchangeRateSuccessfully() {
    // Given
    double expectedRate = 91.25;
    when(mockProvider.getExchangeRate(AMOUNT, FROM, TO)).thenReturn(expectedRate);

    // When
    double actualRate = currencyExchangeService.getExchangeRate(AMOUNT, FROM, TO);

    // Then
    assertEquals(expectedRate, actualRate);
    verify(mockProvider, times(1)).getExchangeRate(AMOUNT, FROM, TO);
  }

  @Test
  void testWithInvalidProviderAndShouldThrowBadRequestException() {
    // Given
    String invalidProvider = "invalid-provider";
    CurrencyExchangeServiceImpl serviceWithInvalidProvider = new CurrencyExchangeServiceImpl(
        invalidProvider,
        List.of(mockProvider)
    );

    // When
    Exception exception = assertThrows(
        BadRequestException.class,
        () -> serviceWithInvalidProvider.getExchangeRate(AMOUNT, FROM, TO)
    );

    // Then
    assertTrue(exception.getMessage().contains("Invalid exchange provider"));
  }
}
