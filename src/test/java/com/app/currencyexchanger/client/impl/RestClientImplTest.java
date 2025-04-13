package com.app.currencyexchanger.client.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.currencyexchanger.exception.BadRequestException;
import com.app.currencyexchanger.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.RestTemplate;

class RestClientImplTest {

  private RestTemplate restTemplate;
  private RestClientImpl restClient;

  private static final String TEST_URL = "http://test.com/api";

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    restClient = new RestClientImpl(restTemplate);
  }

  @Test
  void testWithSuccessfulResponseAndShouldReturnResponseBody() {
    // Given
    String request = "input";
    String expectedResponse = "response";
    ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
    when(restTemplate.exchange(eq(TEST_URL), eq(HttpMethod.POST), any(), eq(String.class)))
        .thenReturn(mockResponse);
    // When
    String result = restClient.exchange(TEST_URL, HttpMethod.POST, request, String.class);

    // Then
    assertEquals(expectedResponse, result);
    verify(restTemplate).exchange(eq(TEST_URL), eq(HttpMethod.POST), any(), eq(String.class));
  }

  @Test
  void testWithHttpClientBadRequestAndShouldThrowCustomBadRequestException() {
    // Given
    String request = "input";
    HttpClientErrorException.BadRequest badRequestException = (BadRequest) BadRequest.create(
        HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, "Validation failed" .getBytes(),
        null);
    when(restTemplate.exchange(eq(TEST_URL), eq(HttpMethod.POST), any(), eq(String.class)))
        .thenThrow(badRequestException);
    // When & Then
    BadRequestException exception = assertThrows(BadRequestException.class, () ->
        restClient.exchange(TEST_URL, HttpMethod.POST, request, String.class)
    );
    assertTrue(exception.getMessage().contains("Validation failed"));
  }

  @Test
  void testWithUnexpectedExceptionAndShouldThrowInternalServerException() {
    // Given
    String request = "input";
    when(restTemplate.exchange(eq(TEST_URL), eq(HttpMethod.POST), any(), eq(String.class)))
        .thenThrow(new RuntimeException("Connection timeout"));
    // When & Then
    InternalServerException exception = assertThrows(InternalServerException.class, () ->
        restClient.exchange(TEST_URL, HttpMethod.POST, request, String.class)
    );
    assertTrue(exception.getMessage().contains("System error calling URL"));
  }

  @Test
  void testWithNullResponseBodyAndShouldReturnNull() {
    // Given
    String request = "input";
    ResponseEntity<String> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);
    when(restTemplate.exchange(eq(TEST_URL), eq(HttpMethod.POST), any(), eq(String.class)))
        .thenReturn(mockResponse);
    // When
    String result = restClient.exchange(TEST_URL, HttpMethod.POST, request, String.class);
    // Then
    assertNull(result);
  }

  @Test
  void testWithRequestBodyAndShouldSetHeadersCorrectly() {
    // Given
    String request = "input";
    String expectedResponse = "response";
    ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
    when(restTemplate.exchange(eq(TEST_URL), eq(HttpMethod.POST), captor.capture(),
        eq(String.class)))
        .thenReturn(mockResponse);
    // When
    restClient.exchange(TEST_URL, HttpMethod.POST, request, String.class);
    // Then
    HttpEntity capturedEntity = captor.getValue();
    assertEquals(request, capturedEntity.getBody());
    HttpHeaders headers = capturedEntity.getHeaders();
    assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    assertTrue(headers.getAccept().contains(MediaType.APPLICATION_JSON));
  }
}
