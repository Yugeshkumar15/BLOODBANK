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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "issue_records")
public class IssueRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_unit_id", nullable = false)
    @NotNull(message = "Blood unit must not be null")
    private BloodUnit bloodUnit;

    @NotBlank(message = "Patient name must not be blank")
    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @NotNull(message = "Blood group must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false)
    private BloodGroup bloodGroup;

    @NotNull(message = "Units issued must not be null")
    @Min(value = 1, message = "Units issued must be at least 1")
    @Column(name = "units_issued", nullable = false)
    private Integer unitsIssued;

    @NotNull(message = "Issue date must not be null")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    public IssueRecord() {
    }

    public IssueRecord(BloodUnit bloodUnit, String patientName, BloodGroup bloodGroup, Integer unitsIssued, LocalDate issueDate) {
        this.bloodUnit = bloodUnit;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.unitsIssued = unitsIssued;
        this.issueDate = issueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloodUnit getBloodUnit() {
        return bloodUnit;
    }

    public void setBloodUnit(BloodUnit bloodUnit) {
        this.bloodUnit = bloodUnit;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Integer getUnitsIssued() {
        return unitsIssued;
    }

    public void setUnitsIssued(Integer unitsIssued) {
        this.unitsIssued = unitsIssued;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
