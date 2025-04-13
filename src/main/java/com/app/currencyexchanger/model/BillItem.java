package com.app.currencyexchanger.model;

import com.app.currencyexchanger.enums.ItemCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "Item in a bill")
public class BillItem implements Serializable {

  private String name;
  private ItemCategory category; // GROCERIES, ELECTRONICS, etc.
  private BigDecimal price;
  private int quantity;

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ItemCategory getCategory() {
    return category;
  }

  public void setCategory(ItemCategory category) {
    this.category = category;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}