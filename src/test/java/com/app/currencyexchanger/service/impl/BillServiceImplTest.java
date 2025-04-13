package com.app.currencyexchanger.service.impl;

import static com.app.currencyexchanger.enums.Role.AFFILIATE;
import static com.app.currencyexchanger.enums.Role.CUSTOMER;
import static com.app.currencyexchanger.enums.Role.EMPLOYEE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.app.currencyexchanger.enums.ItemCategory;
import com.app.currencyexchanger.model.BillItem;
import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;
import com.app.currencyexchanger.model.User;
import com.app.currencyexchanger.service.CurrencyExchangeService;
import com.app.currencyexchanger.util.SecurityUtil;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

  @Mock
  private CurrencyExchangeService currencyExchangeService;

  @Mock
  private User currentUser;

  @InjectMocks
  private BillServiceImpl billService;

  private MockedStatic<SecurityUtil> mockedSecurityUtil;

  @BeforeEach
  void setUp() {
    mockedSecurityUtil = mockStatic(SecurityUtil.class);
  }

  @AfterEach
  void tearDown() {
    mockedSecurityUtil.close();
  }

  @Test
  void testWithNullUserShouldThrowSecurityException() {
    // Given
    BillRequest request = new BillRequest();
    given(SecurityUtil.getCurrentUser()).willReturn(null);

    // When & Then
    assertThrows(SecurityException.class, () -> billService.calculateNetPayable(request));
  }

  @Test
  void testWithGroceryItemsOnlyShouldApplyOnlyHundredDollarDiscount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem groceryItem = new BillItem();
    groceryItem.setCategory(ItemCategory.GROCERIES);
    groceryItem.setPrice(BigDecimal.valueOf(150));
    groceryItem.setQuantity(2);
    request.setItems(List.of(groceryItem));
    request.setOriginalCurrency("USD");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("300.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("285.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("285.00"), response.getFinalAmount());
  }

  @Test
  void testWithNonGroceryItemsAndEmployeeRoleShouldApplyEmployeeDiscount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem electronicItem = new BillItem();
    electronicItem.setCategory(ItemCategory.ELECTRONICS);
    electronicItem.setPrice(BigDecimal.valueOf(100));
    electronicItem.setQuantity(3);
    request.setItems(List.of(electronicItem));
    request.setOriginalCurrency("USD");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(EMPLOYEE);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("300.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("195.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("195.00"), response.getFinalAmount());
  }

  @Test
  void testWithNonGroceryItemsAndAffiliateRoleShouldApplyAffiliateDiscount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem electronicItem = new BillItem();
    electronicItem.setCategory(ItemCategory.ELECTRONICS);
    electronicItem.setPrice(BigDecimal.valueOf(100));
    electronicItem.setQuantity(3);
    request.setItems(List.of(electronicItem));
    request.setOriginalCurrency("USD");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(AFFILIATE);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("300.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("255.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("255.00"), response.getFinalAmount());
  }

  @Test
  void testWithNonGroceryItemsAndLongTermCustomerShouldApplyCustomerDiscount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem electronicItem = new BillItem();
    electronicItem.setCategory(ItemCategory.ELECTRONICS);
    electronicItem.setPrice(BigDecimal.valueOf(100));
    electronicItem.setQuantity(3);
    request.setItems(List.of(electronicItem));
    request.setOriginalCurrency("USD");

    Calendar customerSince = Calendar.getInstance();
    customerSince.add(Calendar.YEAR, -3);

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(CUSTOMER);
    given(currentUser.getCustomerSince()).willReturn(customerSince.getTime());

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("300.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("270.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("270.00"), response.getFinalAmount());
  }

  @Test
  void testWithNonGroceryItemsAndNewCustomerShouldNotApplyCustomerDiscount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem electronicItem = new BillItem();
    electronicItem.setCategory(ItemCategory.ELECTRONICS);
    electronicItem.setPrice(BigDecimal.valueOf(100));
    electronicItem.setQuantity(3);
    request.setItems(List.of(electronicItem));
    request.setOriginalCurrency("USD");

    Calendar customerSince = Calendar.getInstance();
    customerSince.add(Calendar.YEAR, -1);

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(CUSTOMER);
    given(currentUser.getCustomerSince()).willReturn(customerSince.getTime());

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("300.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("285.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("285.00"), response.getFinalAmount());
  }

  @Test
  void testWithCurrencyConversionShouldConvertFinalAmount() {
    // Given
    BillRequest request = new BillRequest();
    BillItem item = new BillItem();
    item.setCategory(ItemCategory.ELECTRONICS);
    item.setPrice(BigDecimal.valueOf(100));
    item.setQuantity(1);
    request.setItems(List.of(item));
    request.setOriginalCurrency("USD");
    request.setTargetCurrency("EUR");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(EMPLOYEE);
    given(currencyExchangeService.getExchangeRate(any(), eq("USD"), eq("EUR")))
        .willReturn(0.85);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("100.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("65.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("55.25"), response.getFinalAmount());
  }

  @Test
  void testWithMixedItemsShouldApplyCorrectDiscounts() {
    // Given
    BillRequest request = new BillRequest();

    BillItem groceryItem = new BillItem();
    groceryItem.setCategory(ItemCategory.GROCERIES);
    groceryItem.setPrice(BigDecimal.valueOf(50));
    groceryItem.setQuantity(2);

    BillItem electronicItem = new BillItem();
    electronicItem.setCategory(ItemCategory.ELECTRONICS);
    electronicItem.setPrice(BigDecimal.valueOf(300));
    electronicItem.setQuantity(1);

    request.setItems(List.of(groceryItem, electronicItem));
    request.setOriginalCurrency("USD");

    Calendar customerSince = Calendar.getInstance();
    customerSince.add(Calendar.YEAR, -3);

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(CUSTOMER);
    given(currentUser.getCustomerSince()).willReturn(customerSince.getTime());

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("400.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("360.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("360.00"), response.getFinalAmount());
  }

  @Test
  void testWithLargeBillAmountShouldApplyMultipleHundredDollarDiscounts() {
    // Given
    BillRequest request = new BillRequest();
    BillItem item = new BillItem();
    item.setCategory(ItemCategory.ELECTRONICS);
    item.setPrice(BigDecimal.valueOf(1000));
    item.setQuantity(1);
    request.setItems(List.of(item));
    request.setOriginalCurrency("USD");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(EMPLOYEE);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals(new BigDecimal("1000.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("650.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("650.00"), response.getFinalAmount());
  }

  @Test
  void testWithNullTargetCurrencyShouldNotConvertCurrency() {
    // Given
    BillRequest request = new BillRequest();
    BillItem item = new BillItem();
    item.setCategory(ItemCategory.ELECTRONICS);
    item.setPrice(BigDecimal.valueOf(100));
    item.setQuantity(1);
    request.setItems(List.of(item));
    request.setOriginalCurrency("USD");
    request.setTargetCurrency(null);

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(EMPLOYEE);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals("USD", response.getTargetCurrency());
    assertEquals(new BigDecimal("100.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("65.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("65.00"), response.getFinalAmount());
  }

  @Test
  void testWithSameOriginalAndTargetCurrencyShouldNotConvertCurrency() {
    // Given
    BillRequest request = new BillRequest();
    BillItem item = new BillItem();
    item.setCategory(ItemCategory.ELECTRONICS);
    item.setPrice(BigDecimal.valueOf(100));
    item.setQuantity(1);
    request.setItems(List.of(item));
    request.setOriginalCurrency("USD");
    request.setTargetCurrency("USD");

    given(SecurityUtil.getCurrentUser()).willReturn(currentUser);
    given(currentUser.getUsername()).willReturn("testUser");
    given(currentUser.getRole()).willReturn(EMPLOYEE);

    // When
    BillResponse response = billService.calculateNetPayable(request);

    // Then
    assertEquals("USD", response.getTargetCurrency());
    assertEquals(new BigDecimal("100.00"), response.getOriginalAmount());
    assertEquals(new BigDecimal("65.00"), response.getDiscountedAmount());
    assertEquals(new BigDecimal("65.00"), response.getFinalAmount());
  }
}