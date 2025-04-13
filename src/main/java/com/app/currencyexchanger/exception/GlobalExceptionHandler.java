package com.app.currencyexchanger.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequest(
      BadRequestException ex, HttpServletRequest request
  ) {
    LOGGER.warn("BadRequestException: {}", ex.getMessage());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<Object> handleInternalServer(
      InternalServerException ex, HttpServletRequest request
  ) {
    LOGGER.error("InternalServerException: {}", ex.getMessage(), ex);
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneral(Exception ex, HttpServletRequest request) {
    LOGGER.error("Unhandled Exception: {}", ex.getMessage(), ex);
    return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong",
        request.getRequestURI());
  }

  private ResponseEntity<Object> buildResponseEntity(
      HttpStatus status, String message, String path
  ) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("path", path);
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
  }
}
