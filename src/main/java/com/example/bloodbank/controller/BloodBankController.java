package com.example.bloodbank.controller;

import com.example.bloodbank.dto.BloodUnitResponse;
import com.example.bloodbank.dto.DonationRequest;
import com.example.bloodbank.dto.DonationResponse;
import com.example.bloodbank.dto.EligibilityResponse;
import com.example.bloodbank.dto.IssueRequest;
import com.example.bloodbank.dto.IssueResponse;
import com.example.bloodbank.dto.StockResponse;
import com.example.bloodbank.service.BloodBankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bloodbank")
public class BloodBankController {

    private final BloodBankService bloodBankService;

    public BloodBankController(BloodBankService bloodBankService) {
        this.bloodBankService = bloodBankService;
    }

    @PostMapping("/donations")
    public ResponseEntity<DonationResponse> recordDonation(@Valid @RequestBody DonationRequest request) {
        DonationResponse response = bloodBankService.recordDonation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/donors/{donorId}/eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(@PathVariable Long donorId) {
        EligibilityResponse response = bloodBankService.checkDonorEligibility(donorId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueResponse> issueBlood(@Valid @RequestBody IssueRequest request) {
        IssueResponse response = bloodBankService.issueBlood(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/stock")
    public ResponseEntity<StockResponse> getCurrentStock() {
        StockResponse response = bloodBankService.getCurrentStock();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<BloodUnitResponse>> getExpiringUnits() {
        List<BloodUnitResponse> response = bloodBankService.getExpiringUnits();
        return ResponseEntity.ok(response);
    }
}
