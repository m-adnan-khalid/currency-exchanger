package com.app.currencyexchanger.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "Response model for bill calculation")
public class BillResponse implements Serializable {

  private BigDecimal originalAmount;
  private BigDecimal discountedAmount;
  private BigDecimal finalAmount;
  private String originalCurrency;
  private String targetCurrency;

  // Getters and Setters
  public BigDecimal getOriginalAmount() {
    return originalAmount;
  }

  public void setOriginalAmount(BigDecimal originalAmount) {
    this.originalAmount = originalAmount;
  }

  public BigDecimal getDiscountedAmount() {
    return discountedAmount;
  }

  public void setDiscountedAmount(BigDecimal discountedAmount) {
    this.discountedAmount = discountedAmount;
  }

  public BigDecimal getFinalAmount() {
    return finalAmount;
  }

  public void setFinalAmount(BigDecimal finalAmount) {
    this.finalAmount = finalAmount;
  }

  public String getOriginalCurrency() {
    return originalCurrency;
  }

  public void setOriginalCurrency(String originalCurrency) {
    this.originalCurrency = originalCurrency;
  }

  public String getTargetCurrency() {
    return targetCurrency;
  }

  public void setTargetCurrency(String targetCurrency) {
    this.targetCurrency = targetCurrency;
  }
}