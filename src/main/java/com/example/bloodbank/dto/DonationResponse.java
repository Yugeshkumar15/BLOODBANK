package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import com.example.bloodbank.entity.Donation;

import java.time.LocalDate;

public class DonationResponse {

    private Long id;
    private Long donorId;
    private String donorName;
    private BloodGroup bloodGroup;
    private LocalDate collectionDate;
    private LocalDate expiryDate;
    private Integer units;
    private String message;

    public DonationResponse() {
    }

    public DonationResponse(Long id, Long donorId, String donorName, BloodGroup bloodGroup, LocalDate collectionDate, LocalDate expiryDate, Integer units, String message) {
        this.id = id;
        this.donorId = donorId;
        this.donorName = donorName;
        this.bloodGroup = bloodGroup;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.units = units;
        this.message = message;
    }

    public static DonationResponse fromEntity(Donation donation) {
        return new DonationResponse(
            donation.getId(),
            donation.getDonor().getId(),
            donation.getDonor().getName(),
            donation.getDonor().getBloodGroup(),
            donation.getCollectionDate(),
            donation.getExpiryDate(),
            donation.getUnits(),
            "Donation recorded successfully."
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
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

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
