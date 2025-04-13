package com.app.currencyexchanger.client;

import org.springframework.http.HttpMethod;

public interface RestClient {

  <T, R> R exchange(String url, HttpMethod method, T requestBody, Class<R> responseType);

}
