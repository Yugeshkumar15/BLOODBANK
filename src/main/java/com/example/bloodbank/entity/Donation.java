package com.example.bloodbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    @NotNull(message = "Donor must not be null")
    private Donor donor;

    @NotNull(message = "Collection date must not be null")
    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @NotNull(message = "Expiry date must not be null")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "Units must not be null")
    @Min(value = 1, message = "Units must be at least 1")
    @Column(nullable = false)
    private Integer units;

    public Donation() {
    }

    public Donation(Donor donor, LocalDate collectionDate, LocalDate expiryDate, Integer units) {
        this.donor = donor;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.units = units;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
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
}
