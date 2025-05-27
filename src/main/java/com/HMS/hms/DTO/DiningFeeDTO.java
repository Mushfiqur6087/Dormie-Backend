package com.HMS.hms.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DiningFeeDTO {
    
    private Long id;
    
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(resident)$", message = "Type must be 'resident' (dining fees are only for resident students)")
    private String type = "resident"; // Always "resident" for dining fees
    
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2030, message = "Year must be 2030 or earlier")
    private Integer year;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Fee is required")
    @DecimalMin(value = "0.01", message = "Fee must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Fee must be less than 1,000,000")
    private BigDecimal fee;

    // Default constructor
    public DiningFeeDTO() {}

    // Constructor without ID (for creation) - type is always resident
    public DiningFeeDTO(String type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.type = "resident"; // Always resident for dining fees
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    // Constructor with ID (for responses) - type is always resident
    public DiningFeeDTO(Long id, String type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.id = id;
        this.type = "resident"; // Always resident for dining fees
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = "resident"; // Always resident for dining fees
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "DiningFeeDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", year=" + year +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", fee=" + fee +
                '}';
    }
}
