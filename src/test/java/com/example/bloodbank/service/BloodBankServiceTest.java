package com.example.bloodbank.service;

import com.example.bloodbank.dto.BloodUnitResponse;
import com.example.bloodbank.dto.DonationRequest;
import com.example.bloodbank.dto.DonationResponse;
import com.example.bloodbank.dto.EligibilityResponse;
import com.example.bloodbank.dto.DonorRequest;
import com.example.bloodbank.dto.DonorResponse;
import com.example.bloodbank.dto.IssueRequest;
import com.example.bloodbank.dto.IssueResponse;
import com.example.bloodbank.dto.StockResponse;
import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.exception.BusinessException;
import com.example.bloodbank.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BloodBankServiceTest {

    @Autowired
    private DonorService donorService;

    @Autowired
    private BloodBankService bloodBankService;

    @Test
    void testCreateDonorAndDuplicatePhoneCheck() {
        DonorRequest req1 = new DonorRequest("Arun Kumar", "9876543210", 25, BloodGroup.O_POSITIVE);
        DonorResponse donor1 = donorService.createDonor(req1);

        assertNotNull(donor1.getId());
        assertEquals("Arun Kumar", donor1.getName());

        // Duplicate phone check
        DonorRequest reqDuplicate = new DonorRequest("Vijay", "9876543210", 30, BloodGroup.A_POSITIVE);
        BusinessException ex = assertThrows(BusinessException.class, () -> donorService.createDonor(reqDuplicate));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void testDonorEligibilityAndDonationRules() {
        // Create Donor
        DonorRequest req = new DonorRequest("Rohan", "9988776655", 30, BloodGroup.A_POSITIVE);
        DonorResponse donor = donorService.createDonor(req);

        // Initially eligible (no previous donation)
        EligibilityResponse elig1 = bloodBankService.checkDonorEligibility(donor.getId());
        assertTrue(elig1.isEligible());

        // 1st Donation on 2026-06-01
        LocalDate firstDate = LocalDate.of(2026, 6, 1);
        DonationRequest don1 = new DonationRequest(donor.getId(), firstDate, 2);
        DonationResponse res1 = bloodBankService.recordDonation(don1);

        assertNotNull(res1.getId());
        assertEquals(firstDate.plusDays(42), res1.getExpiryDate());

        // Attempt 2nd donation only 60 days later -> Should be rejected (< 90 days gap)
        LocalDate secondDate = firstDate.plusDays(60);
        DonationRequest don2 = new DonationRequest(donor.getId(), secondDate, 1);
        BusinessException ex = assertThrows(BusinessException.class, () -> bloodBankService.recordDonation(don2));
        assertTrue(ex.getMessage().contains("Donor must wait at least 90 days"));

        // Attempt 2nd donation 95 days later -> Should succeed (>= 90 days gap)
        LocalDate thirdDate = firstDate.plusDays(95);
        DonationRequest don3 = new DonationRequest(donor.getId(), thirdDate, 2);
        DonationResponse res3 = bloodBankService.recordDonation(don3);
        assertNotNull(res3.getId());
    }

    @Test
    void testStockFEFOAndIssueBlood() {
        // Create Donor
        DonorRequest req = new DonorRequest("Suresh", "9123456789", 28, BloodGroup.O_POSITIVE);
        DonorResponse donor = donorService.createDonor(req);

        // Record donation today
        LocalDate today = LocalDate.now();
        DonationRequest donReq = new DonationRequest(donor.getId(), today, 5);
        bloodBankService.recordDonation(donReq);

        // Check stock
        StockResponse stockBefore = bloodBankService.getCurrentStock();
        assertEquals(5, stockBefore.getStock().get(BloodGroup.O_POSITIVE));

        // Issue 2 units
        IssueRequest issueReq = new IssueRequest(BloodGroup.O_POSITIVE, "Patient John", 2);
        IssueResponse issueRes = bloodBankService.issueBlood(issueReq);

        assertEquals(2, issueRes.getTotalUnitsIssued());
        assertEquals("Patient John", issueRes.getPatientName());

        // Check stock after issue
        StockResponse stockAfter = bloodBankService.getCurrentStock();
        assertEquals(3, stockAfter.getStock().get(BloodGroup.O_POSITIVE));
    }

    @Test
    void testInsufficientStockRejection() {
        IssueRequest issueReq = new IssueRequest(BloodGroup.AB_NEGATIVE, "Patient Neha", 10);
        BusinessException ex = assertThrows(BusinessException.class, () -> bloodBankService.issueBlood(issueReq));
        assertTrue(ex.getMessage().contains("Insufficient usable stock"));
    }

    @Test
    void testExpiringUnitsAndStockGrouping() {
        DonorRequest req = new DonorRequest("Priya", "9000011111", 24, BloodGroup.B_POSITIVE);
        DonorResponse donor = donorService.createDonor(req);

        // Record donation 38 days ago so expiry (42 days) is in 4 days (within 7 days)
        LocalDate pastDate = LocalDate.now().minusDays(38);
        DonationRequest donReq = new DonationRequest(donor.getId(), pastDate, 3);
        bloodBankService.recordDonation(donReq);

        List<BloodUnitResponse> expiring = bloodBankService.getExpiringUnits();
        assertFalse(expiring.isEmpty());
        assertTrue(expiring.stream().anyMatch(u -> u.getBloodGroup() == BloodGroup.B_POSITIVE));
    }
}
