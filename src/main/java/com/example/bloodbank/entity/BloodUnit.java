package com.example.bloodbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "blood_units")
public class BloodUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    @NotNull(message = "Donation must not be null")
    private Donation donation;

    @NotNull(message = "Blood group must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    @NotNull(message = "Collection date must not be null")
    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @NotNull(message = "Expiry date must not be null")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "Total units must not be null")
    @Min(value = 1, message = "Total units must be at least 1")
    @Column(name = "total_units", nullable = false)
    private Integer totalUnits;

    @NotNull(message = "Available units must not be null")
    @Min(value = 0, message = "Available units cannot be negative")
    @Column(name = "available_units", nullable = false)
    private Integer availableUnits;

    public BloodUnit() {
    }

    public BloodUnit(Donation donation, BloodGroup bloodGroup, LocalDate collectionDate, LocalDate expiryDate, Integer totalUnits, Integer availableUnits) {
        this.donation = donation;
        this.bloodGroup = bloodGroup;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
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
}
