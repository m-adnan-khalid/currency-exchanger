package com.app.currencyexchanger.providers;

import com.app.currencyexchanger.client.RestClient;
import com.app.currencyexchanger.exception.InternalServerException;
import com.app.currencyexchanger.model.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OpenExchangeRatesProviderTest {

  @Mock
  private RestClient restClient;

  @InjectMocks
  private OpenExchangeRatesProvider openExchangeRatesProvider;

  @Mock
  private ExchangeRateResponse exchangeRateResponseMock;

  private static final String API_URL = "http://api.openexchangerates.org";
  private static final String APP_ID = "testAppId";
  private static final String FROM_CURRENCY = "USD";
  private static final String TO_CURRENCY = "EUR";
  private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.00);
  private static final double EXPECTED_CONVERSION_RESULT = 85.50;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Mocking the value injection from application.properties
    openExchangeRatesProvider.apiUrl = API_URL;
    openExchangeRatesProvider.appId = APP_ID;
  }

  @Test
  void testGetExchangeRateShouldReturnConversionResult() {
    // Given
    String url = String.format("%s/convert/%s/%s/%s?app_id=%s&prettyprint=false", API_URL, AMOUNT.stripTrailingZeros().toPlainString(), FROM_CURRENCY, TO_CURRENCY, APP_ID);
    given(restClient.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class)))
        .willReturn(exchangeRateResponseMock);
    given(exchangeRateResponseMock.getResponse()).willReturn(EXPECTED_CONVERSION_RESULT);

    // When
    double conversionRate = openExchangeRatesProvider.getExchangeRate(AMOUNT, FROM_CURRENCY, TO_CURRENCY);

    // Then
    assertEquals(EXPECTED_CONVERSION_RESULT, conversionRate);
    verify(restClient).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class));
  }

  @Test
  void testGetExchangeRateShouldThrowExceptionWhenResponseIsNull() {
    // Given
    String url = String.format("%s/convert/%s/%s/%s?app_id=%s&prettyprint=false", API_URL, AMOUNT.stripTrailingZeros().toPlainString(), FROM_CURRENCY, TO_CURRENCY, APP_ID);
    given(restClient.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class)))
        .willReturn(null);

    // When & Then
    InternalServerException exception = assertThrows(InternalServerException.class, () ->
        openExchangeRatesProvider.getExchangeRate(AMOUNT, FROM_CURRENCY, TO_CURRENCY)
    );

    verify(restClient).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class));
  }

  @Test
  void testGetExchangeRateShouldThrowExceptionWhenApiFails() {
    // Given
    String url = String.format("%s/convert/%s/%s/%s?app_id=%s&prettyprint=false", API_URL, AMOUNT.stripTrailingZeros().toPlainString(), FROM_CURRENCY, TO_CURRENCY, APP_ID);
    given(restClient.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class)))
        .willThrow(new RuntimeException("API failure"));

    // When & Then
    InternalServerException exception = assertThrows(InternalServerException.class, () ->
        openExchangeRatesProvider.getExchangeRate(AMOUNT, FROM_CURRENCY, TO_CURRENCY)
    );

    assertEquals("Failed to fetch exchange rate from provider: openexchangerates", exception.getMessage());
    verify(restClient).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateResponse.class));
  }

  @Test
  void testGetProviderNameShouldReturnCorrectProviderName() {
    // Given & When
    String providerName = openExchangeRatesProvider.getProviderName();

    // Then
    assertEquals("openexchangerates", providerName);
  }
}
