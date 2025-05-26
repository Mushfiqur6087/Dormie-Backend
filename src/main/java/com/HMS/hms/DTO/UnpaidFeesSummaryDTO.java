package com.HMS.hms.DTO;

import java.math.BigDecimal;

public class UnpaidFeesSummaryDTO {
    private BigDecimal totalUnpaidAmount;
    private String email;
    private String username;
    private String feeType; // "HALL" or "DINING"
    private String feeDescription; // "Hall Fees" or "Dining Fees"

    // Default constructor
    public UnpaidFeesSummaryDTO() {}

    // Constructor for backward compatibility
    public UnpaidFeesSummaryDTO(BigDecimal totalUnpaidAmount, String email, String username) {
        this.totalUnpaidAmount = totalUnpaidAmount;
        this.email = email;
        this.username = username;
    }

    // Constructor with fee type only
    public UnpaidFeesSummaryDTO(BigDecimal totalUnpaidAmount, String email, String username, String feeType) {
        this.totalUnpaidAmount = totalUnpaidAmount;
        this.email = email;
        this.username = username;
        this.feeType = feeType;
    }

    // Constructor with fee type and description
    public UnpaidFeesSummaryDTO(BigDecimal totalUnpaidAmount, String email, String username, String feeType, String feeDescription) {
        this.totalUnpaidAmount = totalUnpaidAmount;
        this.email = email;
        this.username = username;
        this.feeType = feeType;
        this.feeDescription = feeDescription;
    }

    // Getters and Setters
    public BigDecimal getTotalUnpaidAmount() {
        return totalUnpaidAmount;
    }

    public void setTotalUnpaidAmount(BigDecimal totalUnpaidAmount) {
        this.totalUnpaidAmount = totalUnpaidAmount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }

    @Override
    public String toString() {
        return "UnpaidFeesSummaryDTO{" +
                "totalUnpaidAmount=" + totalUnpaidAmount +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", feeType='" + feeType + '\'' +
                ", feeDescription='" + feeDescription + '\'' +
                '}';
    }
}
