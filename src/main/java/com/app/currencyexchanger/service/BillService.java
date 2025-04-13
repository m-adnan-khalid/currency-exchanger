package com.app.currencyexchanger.service;

import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;

/**
 * Service interface for handling billing operations.
 * <p>
 * This interface defines methods for calculating net payable amounts for bill requests.
 * </p>
 */
public interface BillService {

  /**
   * Calculates the net payable amount based on the provided bill request.
   * <p>
   * This method processes the bill details, applies necessary calculations (e.g., tax, discounts),
   * and returns the final net payable amount.
   * </p>
   *
   * @param request the bill request containing item details, currency, and other billing
   *                information
   * @return a {@link BillResponse} containing the calculated net payable amount
   */
  BillResponse calculateNetPayable(BillRequest request);

}
