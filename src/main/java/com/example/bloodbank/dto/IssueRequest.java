package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class IssueRequest {

    @NotNull(message = "Blood group must not be null")
    private BloodGroup bloodGroup;

    @NotBlank(message = "Patient name must not be blank")
    private String patientName;

    @NotNull(message = "Units must not be null")
    @Min(value = 1, message = "Units must be at least 1")
    private Integer units;

    private LocalDate issueDate;

    public IssueRequest() {
    }

    public IssueRequest(BloodGroup bloodGroup, String patientName, Integer units) {
        this.bloodGroup = bloodGroup;
        this.patientName = patientName;
        this.units = units;
    }

    public IssueRequest(BloodGroup bloodGroup, String patientName, Integer units, LocalDate issueDate) {
        this.bloodGroup = bloodGroup;
        this.patientName = patientName;
        this.units = units;
        this.issueDate = issueDate;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
