package com.app.currencyexchanger.service;

import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;

public interface BillService {

  BillResponse calculateNetPayable(BillRequest request);

}
