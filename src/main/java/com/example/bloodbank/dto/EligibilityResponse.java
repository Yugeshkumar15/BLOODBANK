package com.example.bloodbank.dto;

import java.time.LocalDate;

public class EligibilityResponse {

    private Long donorId;
    private boolean eligible;
    private String message;
    private LocalDate lastDonationDate;
    private Long daysSinceLastDonation;
    private int minimumGapDays;

    public EligibilityResponse() {
    }

    public EligibilityResponse(Long donorId, boolean eligible, String message, LocalDate lastDonationDate, Long daysSinceLastDonation, int minimumGapDays) {
        this.donorId = donorId;
        this.eligible = eligible;
        this.message = message;
        this.lastDonationDate = lastDonationDate;
        this.daysSinceLastDonation = daysSinceLastDonation;
        this.minimumGapDays = minimumGapDays;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public Long getDaysSinceLastDonation() {
        return daysSinceLastDonation;
    }

    public void setDaysSinceLastDonation(Long daysSinceLastDonation) {
        this.daysSinceLastDonation = daysSinceLastDonation;
    }

    public int getMinimumGapDays() {
        return minimumGapDays;
    }

    public void setMinimumGapDays(int minimumGapDays) {
        this.minimumGapDays = minimumGapDays;
    }
}
