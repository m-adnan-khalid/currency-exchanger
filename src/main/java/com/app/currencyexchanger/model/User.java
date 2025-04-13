package com.app.currencyexchanger.model;

import com.app.currencyexchanger.enums.Role;
import java.util.Date;

public class User {

  private String username;
  private Role role;

  private Date customerSince;  // New field for customer tenure

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Date getCustomerSince() {
    return customerSince;
  }

  public void setCustomerSince(Date customerSince) {
    this.customerSince = customerSince;
  }
}
