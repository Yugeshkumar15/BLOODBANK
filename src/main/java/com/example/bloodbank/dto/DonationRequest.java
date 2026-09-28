package com.example.bloodbank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class DonationRequest {

    @NotNull(message = "Donor ID must not be null")
    private Long donorId;

    @NotNull(message = "Collection date must not be null")
    private LocalDate collectionDate;

    @NotNull(message = "Units must not be null")
    @Min(value = 1, message = "Units must be at least 1")
    private Integer units;

    public DonationRequest() {
    }

    public DonationRequest(Long donorId, LocalDate collectionDate, Integer units) {
        this.donorId = donorId;
        this.collectionDate = collectionDate;
        this.units = units;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }
}
