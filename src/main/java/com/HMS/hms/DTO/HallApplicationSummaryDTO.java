package com.HMS.hms.DTO;

import java.math.BigDecimal;

public class HallApplicationSummaryDTO {
    private Long applicationId;
    private Long studentIdNo; // The university-assigned student ID
    private String username;    // Student's username (from Users table)
    private String applicationStatus; // E.g., PENDING, APPROVED, REJECTED
    private String familyIncome; // Converted to String for display
    private Double distanceFromHallKm; // Converted to Double for display

    // Constructor to convert from HallApplication entity
    public HallApplicationSummaryDTO(Long applicationId, Long studentIdNo, String username, String applicationStatus, BigDecimal familyIncome, BigDecimal distanceFromHallKm) {
        this.applicationId = applicationId;
        this.studentIdNo = studentIdNo;
        this.username = username;
        this.applicationStatus = applicationStatus;
        this.familyIncome = (familyIncome != null) ? familyIncome.toPlainString() : null; // Convert BigDecimal to String
        this.distanceFromHallKm = (distanceFromHallKm != null) ? distanceFromHallKm.doubleValue() : null; // Convert BigDecimal to Double
    }

    // --- Getters and Setters ---
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public Long getStudentIdNo() { return studentIdNo; }
    public void setStudentIdNo(Long studentIdNo) { this.studentIdNo = studentIdNo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public String getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(String familyIncome) { this.familyIncome = familyIncome; }
    public Double getDistanceFromHallKm() { return distanceFromHallKm; }
    public void setDistanceFromHallKm(Double distanceFromHallKm) { this.distanceFromHallKm = distanceFromHallKm; }
}