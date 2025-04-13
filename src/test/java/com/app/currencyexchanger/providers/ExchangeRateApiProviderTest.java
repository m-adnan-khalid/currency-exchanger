package com.app.currencyexchanger.providers;

import com.app.currencyexchanger.client.RestClient;
import com.app.currencyexchanger.exception.InternalServerException;
import com.app.currencyexchanger.model.ExchangeRateApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateApiProviderTest {

  @Mock
  private RestClient restClient;

  @InjectMocks
  private ExchangeRateApiProvider exchangeRateApiProvider;

  @Mock
  private ExchangeRateApiResponse exchangeRateApiResponseMock;

  private static final String API_URL = "http://api.exchangerateapi.com";
  private static final String API_KEY = "testApiKey";
  private static final String FROM_CURRENCY = "USD";
  private static final String TO_CURRENCY = "EUR";
  private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.00);
  private static final double EXPECTED_CONVERSION_RESULT = 85.50;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Mocking the value injection from application.properties
    exchangeRateApiProvider.apiUrl = API_URL;
    exchangeRateApiProvider.apiKey = API_KEY;
  }

  @Test
  void testGetExchangeRateShouldReturnConversionResult() {
    // Given
    String url = String.format("%s/%s/pair/%s/%s/%s", API_URL, API_KEY, FROM_CURRENCY, TO_CURRENCY, AMOUNT.stripTrailingZeros().toPlainString());
    given(restClient.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateApiResponse.class)))
        .willReturn(exchangeRateApiResponseMock);
    given(exchangeRateApiResponseMock.getConversionResult()).willReturn(EXPECTED_CONVERSION_RESULT);

    // When
    double conversionRate = exchangeRateApiProvider.getExchangeRate(AMOUNT, FROM_CURRENCY, TO_CURRENCY);

    // Then
    assertEquals(EXPECTED_CONVERSION_RESULT, conversionRate);
    verify(restClient).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateApiResponse.class));
  }

  @Test
  void testGetExchangeRateShouldThrowExceptionWhenResponseIsNull() {
    // Given
    String url = String.format("%s/%s/pair/%s/%s/%s", API_URL, API_KEY, FROM_CURRENCY, TO_CURRENCY, AMOUNT.stripTrailingZeros().toPlainString());
    given(restClient.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateApiResponse.class)))
        .willReturn(null);

    // When & Then
    InternalServerException exception = assertThrows(InternalServerException.class, () ->
        exchangeRateApiProvider.getExchangeRate(AMOUNT, FROM_CURRENCY, TO_CURRENCY)
    );

    assertEquals("Empty response received from ExchangeRate-API", exception.getMessage());
    verify(restClient).exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(ExchangeRateApiResponse.class));
  }

  @Test
  void testGetProviderNameShouldReturnCorrectProviderName() {
    // Given & When
    String providerName = exchangeRateApiProvider.getProviderName();

    // Then
    assertEquals("exchangerateapi", providerName);
  }
}
