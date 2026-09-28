package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.entity.BloodUnit;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BloodUnitResponse {

    private Long id;
    private Long donationId;
    private String donorName;
    private BloodGroup bloodGroup;
    private LocalDate collectionDate;
    private LocalDate expiryDate;
    private Integer totalUnits;
    private Integer availableUnits;
    private long daysUntilExpiry;

    public BloodUnitResponse() {
    }

    public BloodUnitResponse(Long id, Long donationId, String donorName, BloodGroup bloodGroup, LocalDate collectionDate, LocalDate expiryDate, Integer totalUnits, Integer availableUnits, long daysUntilExpiry) {
        this.id = id;
        this.donationId = donationId;
        this.donorName = donorName;
        this.bloodGroup = bloodGroup;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public static BloodUnitResponse fromEntity(BloodUnit unit, LocalDate today) {
        long daysRemaining = ChronoUnit.DAYS.between(today, unit.getExpiryDate());
        String donorName = (unit.getDonation() != null && unit.getDonation().getDonor() != null) 
                ? unit.getDonation().getDonor().getName() : "Unknown";
        Long donationId = (unit.getDonation() != null) ? unit.getDonation().getId() : null;

        return new BloodUnitResponse(
            unit.getId(),
            donationId,
            donorName,
            unit.getBloodGroup(),
            unit.getCollectionDate(),
            unit.getExpiryDate(),
            unit.getTotalUnits(),
            unit.getAvailableUnits(),
            daysRemaining
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public long getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(long daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }
}
