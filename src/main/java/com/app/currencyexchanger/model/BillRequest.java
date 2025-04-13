package com.app.currencyexchanger.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(description = "Request model for calculating the bill")
public class BillRequest implements Serializable {

  private List<BillItem> items;
  private String originalCurrency; // e.g., USD
  private String targetCurrency;   // e.g., EUR (optional)

  // Getters and Setters
  public List<BillItem> getItems() {
    return items;
  }

  public void setItems(List<BillItem> items) {
    this.items = items;
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