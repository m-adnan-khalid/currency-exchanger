package com.app.currencyexchanger.client.impl;

import com.app.currencyexchanger.client.RestClient;
import com.app.currencyexchanger.exception.BadRequestException;
import com.app.currencyexchanger.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClientImpl implements RestClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestClientImpl.class);

  private final RestTemplate restTemplate;

  public RestClientImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public <T, R> R exchange(String url, HttpMethod method, T requestBody, Class<R> responseType) {
    LOGGER.info("Making {} request to URL: {}", method, url);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(MediaType.parseMediaTypes("application/json"));
      HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
      LOGGER.debug("Request body: {}", requestBody);
      ResponseEntity<R> response = restTemplate.exchange(url, method, entity, responseType);
      LOGGER.debug("Response body: {}", response.getBody());
      if (response.getStatusCode().is2xxSuccessful()) {
        LOGGER.info("Request to URL {} succeeded with status code: {}", url,
            response.getStatusCode());
        return response.getBody();
      }
    } catch (HttpClientErrorException.BadRequest ex) {
      throw new BadRequestException("Validation failed: " + ex.getResponseBodyAsString());
    } catch (Exception e) {
      throw new InternalServerException("System error calling URL: " + url, e);
    }
    return null;
  }
}
