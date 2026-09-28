package com.example.bloodbank.dto;

import com.example.bloodbank.entity.BloodGroup;

import java.time.LocalDate;
import java.util.List;

public class IssueResponse {

    private String patientName;
    private BloodGroup bloodGroup;
    private Integer totalUnitsIssued;
    private LocalDate issueDate;
    private String message;
    private List<Long> issueRecordIds;

    public IssueResponse() {
    }

    public IssueResponse(String patientName, BloodGroup bloodGroup, Integer totalUnitsIssued, LocalDate issueDate, String message, List<Long> issueRecordIds) {
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.totalUnitsIssued = totalUnitsIssued;
        this.issueDate = issueDate;
        this.message = message;
        this.issueRecordIds = issueRecordIds;
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

    public Integer getTotalUnitsIssued() {
        return totalUnitsIssued;
    }

    public void setTotalUnitsIssued(Integer totalUnitsIssued) {
        this.totalUnitsIssued = totalUnitsIssued;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getIssueRecordIds() {
        return issueRecordIds;
    }

    public void setIssueRecordIds(List<Long> issueRecordIds) {
        this.issueRecordIds = issueRecordIds;
    }
}
