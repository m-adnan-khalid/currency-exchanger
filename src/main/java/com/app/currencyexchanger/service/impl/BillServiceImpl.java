package com.app.currencyexchanger.service.impl;

import static com.app.currencyexchanger.util.SecurityUtil.getCurrentUser;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.app.currencyexchanger.enums.ItemCategory;
import com.app.currencyexchanger.model.BillItem;
import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;
import com.app.currencyexchanger.model.User;
import com.app.currencyexchanger.service.BillService;
import com.app.currencyexchanger.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BillServiceImpl implements BillService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BillServiceImpl.class);

  private final CurrencyExchangeService currencyExchangeService;

  public BillServiceImpl(CurrencyExchangeService currencyExchangeService) {
    this.currencyExchangeService = currencyExchangeService;
  }

  @Override
  public BillResponse calculateNetPayable(BillRequest request) {
    User currentUser = getCurrentUser();
    if (isNull(currentUser)) {
      LOGGER.warn("User not authenticated - throwing SecurityException");
      throw new SecurityException("User not authenticated");
    }
    LOGGER.info("Calculating net payable for user: {}", currentUser.getUsername());
    BigDecimal totalAmount = calculateTotalAmount(request.getItems());
    LOGGER.debug("Total original amount: {}", totalAmount);
    BigDecimal discountedAmount = applyDiscounts(totalAmount, currentUser, request.getItems());
    LOGGER.debug("Discounted amount after user role and bill value deduction: {}",
        discountedAmount);
    BigDecimal finalAmount = shouldConvertCurrency(request)
        ? convertCurrency(discountedAmount, request.getOriginalCurrency(),
        request.getTargetCurrency())
        : discountedAmount;
    if (shouldConvertCurrency(request)) {
      LOGGER.info("Currency converted from {} to {}. Final amount: {}",
          request.getOriginalCurrency(), request.getTargetCurrency(), finalAmount);
    }
    return buildResponse(totalAmount, discountedAmount, finalAmount, request);
  }

  private boolean shouldConvertCurrency(BillRequest request) {
    return nonNull(request.getTargetCurrency()) &&
        !StringUtils.equals(request.getTargetCurrency(), request.getOriginalCurrency());
  }

  private BigDecimal calculateTotalAmount(List<BillItem> items) {
    return items.stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal applyDiscounts(BigDecimal amount, User user, List<BillItem> items) {
    BigDecimal discountedAmount = amount;
    boolean hasNonGroceryItems = items.stream()
        .anyMatch(item -> item.getCategory() != ItemCategory.GROCERIES);
    if (hasNonGroceryItems) {
      LOGGER.debug("User has non-grocery items. Applying role-based discount.");
      discountedAmount = discountedAmount.multiply(BigDecimal.ONE.subtract(
          switch (user.getRole()) {
            case EMPLOYEE -> {
              LOGGER.info("Applying EMPLOYEE discount of 30%");
              yield BigDecimal.valueOf(0.3);
            }
            case AFFILIATE -> {
              LOGGER.info("Applying AFFILIATE discount of 10%");
              yield BigDecimal.valueOf(0.1);
            }
            case CUSTOMER -> {
              if (isLongTermCustomer(user)) {
                LOGGER.info("Applying long-term CUSTOMER discount of 5%");
                yield BigDecimal.valueOf(0.05);
              } else {
                yield BigDecimal.ZERO;
              }
            }
          }
      ));
    }
    BigDecimal hundredDollarDiscounts = amount.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
        .multiply(BigDecimal.valueOf(5));
    if (hundredDollarDiscounts.compareTo(BigDecimal.ZERO) > 0) {
      LOGGER.info("$5 discount applied for every $100 on the bill. Additional discount: {}",
          hundredDollarDiscounts);
    }
    return discountedAmount.subtract(hundredDollarDiscounts).setScale(2, RoundingMode.HALF_UP);
  }

  private boolean isLongTermCustomer(User user) {
    if (user.getCustomerSince() == null) {
      return false;
    }
    Calendar twoYearsAgo = Calendar.getInstance();
    twoYearsAgo.add(Calendar.YEAR, -2);
    return user.getCustomerSince().before(twoYearsAgo.getTime());
  }

  private BigDecimal convertCurrency(
      BigDecimal amount, String fromCurrency, String toCurrency
  ) {
    double rate = currencyExchangeService.getExchangeRate(amount, fromCurrency, toCurrency);
    BigDecimal exchangeRate = BigDecimal.valueOf(rate);
    return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
  }

  private BillResponse buildResponse(
      BigDecimal originalAmount, BigDecimal discountedAmount, BigDecimal finalAmount,
      BillRequest request
  ) {
    BillResponse response = new BillResponse();
    response.setOriginalAmount(originalAmount);
    response.setDiscountedAmount(discountedAmount);
    response.setFinalAmount(finalAmount);
    response.setOriginalCurrency(request.getOriginalCurrency());
    response.setTargetCurrency(
        request.getTargetCurrency() != null ? request.getTargetCurrency()
            : request.getOriginalCurrency()
    );

    LOGGER.info("Bill response built: {}", response);
    return response;
  }

}
