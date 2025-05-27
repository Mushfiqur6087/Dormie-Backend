package com.HMS.hms.DTO;

import java.math.BigDecimal;

public class UnpaidFeesSummaryDTO {
    private BigDecimal totalUnpaidAmount;
    private String email;
    private String username;
    private String feeDescription; // "Hall Fees" or "Dining Fees"

    // Default constructor
    public UnpaidFeesSummaryDTO() {}

    // Constructor for backward compatibility
    public UnpaidFeesSummaryDTO(BigDecimal totalUnpaidAmount, String email, String username) {
        this.totalUnpaidAmount = totalUnpaidAmount;
        this.email = email;
        this.username = username;
    }

    // Constructor with fee description
    public UnpaidFeesSummaryDTO(BigDecimal totalUnpaidAmount, String email, String username, String feeDescription) {
        this.totalUnpaidAmount = totalUnpaidAmount;
        this.email = email;
        this.username = username;
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
                ", feeDescription='" + feeDescription + '\'' +
                '}';
    }
}
