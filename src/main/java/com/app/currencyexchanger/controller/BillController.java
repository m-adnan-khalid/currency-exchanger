package com.app.currencyexchanger.controller;


import static com.app.currencyexchanger.constants.ApiUrl.CALCULATE_BILL_URL;
import static com.app.currencyexchanger.util.ValidationUtils.sanitizeForLogging;

import com.app.currencyexchanger.model.BillRequest;
import com.app.currencyexchanger.model.BillResponse;
import com.app.currencyexchanger.service.BillService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Bill Management", description = "APIs for managing billing operations")
@SecurityRequirement(name = "Authorization")
public class BillController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BillController.class);

  private final BillService billService;

  public BillController(BillService billService) {
    this.billService = billService;
  }

  @PostMapping(value = CALCULATE_BILL_URL)
  public ResponseEntity<BillResponse> calculate(@RequestBody BillRequest request) {
    String sanitizeRequest = sanitizeForLogging(request);
    LOGGER.info("Received request to calculate bill: {}", sanitizeRequest);
    BillResponse response = billService.calculateNetPayable(request);
    LOGGER.info("Bill calculated successfully: {}", response);
    return ResponseEntity.ok(response);
  }

}
