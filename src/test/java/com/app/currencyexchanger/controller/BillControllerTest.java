package com.app.currencyexchanger.controller;

import com.app.currencyexchanger.model.BillItem;
import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;
import com.app.currencyexchanger.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BillControllerTest {

  @Mock
  private BillService billService;

  @InjectMocks
  private BillController billController;

  @Mock
  private Logger logger;

  @Mock
  private BillItem billItemMock;

  @Mock
  private BillResponse billResponseMock;

  private static final String TEST_CURRENCY = "USD";
  private static final String TARGET_CURRENCY = "EUR";
  private static final BigDecimal TEST_ITEM_PRICE = BigDecimal.valueOf(50.0);
  private static final int TEST_ITEM_QUANTITY = 2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testWithValidRequestAndShouldReturnBillResponse() {
    // Given
    BillRequest request = new BillRequest();
    request.setItems(Arrays.asList(billItemMock));
    request.setOriginalCurrency(TEST_CURRENCY);
    request.setTargetCurrency(TARGET_CURRENCY);

    given(billItemMock.getPrice()).willReturn(TEST_ITEM_PRICE);
    given(billItemMock.getQuantity()).willReturn(TEST_ITEM_QUANTITY);

    given(billResponseMock.getOriginalAmount()).willReturn(TEST_ITEM_PRICE.multiply(BigDecimal.valueOf(TEST_ITEM_QUANTITY)));
    given(billResponseMock.getDiscountedAmount()).willReturn(BigDecimal.valueOf(90.0));
    given(billResponseMock.getFinalAmount()).willReturn(BigDecimal.valueOf(85.0));
    given(billResponseMock.getOriginalCurrency()).willReturn(TEST_CURRENCY);
    given(billResponseMock.getTargetCurrency()).willReturn(TARGET_CURRENCY);

    given(billService.calculateNetPayable(request)).willReturn(billResponseMock);

    // When
    ResponseEntity<BillResponse> response = billController.calculate(request);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(billResponseMock, response.getBody());
    verify(billService).calculateNetPayable(request);
  }

  @Test
  void testWithInvalidRequestAndShouldReturnBadRequest() {
    // Given
    BillRequest request = new BillRequest();
    request.setItems(Arrays.asList(billItemMock));
    request.setOriginalCurrency(TEST_CURRENCY);
    request.setTargetCurrency(TARGET_CURRENCY);

    given(billItemMock.getPrice()).willReturn(BigDecimal.valueOf(-50.0));  // Invalid price
    given(billItemMock.getQuantity()).willReturn(TEST_ITEM_QUANTITY);

    // Simulate service exception
    given(billService.calculateNetPayable(request)).willThrow(new IllegalArgumentException("Invalid request"));

    // When & Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        billController.calculate(request)
    );

    assertEquals("Invalid request", exception.getMessage());
    verify(billService).calculateNetPayable(request);
  }
}
