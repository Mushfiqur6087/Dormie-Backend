package com.HMS.hms.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class HallFeeDTO {
    
    private Long id;
    
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(attached|resident)$", message = "Type must be either 'attached' or 'resident'")
    private String type; // "attached" or "resident"
    
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2030, message = "Year must be 2030 or earlier")
    private Integer year;
    
    @NotNull(message = "Fee is required")
    @DecimalMin(value = "0.01", message = "Fee must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Fee must be less than 1,000,000")
    private BigDecimal fee;

    // Default constructor
    public HallFeeDTO() {}

    // Constructor without ID (for creation)
    public HallFeeDTO(String type, Integer year, BigDecimal fee) {
        this.type = type;
        this.year = year;
        this.fee = fee;
    }

    // Constructor with ID (for responses)
    public HallFeeDTO(Long id, String type, Integer year, BigDecimal fee) {
        this.id = id;
        this.type = type;
        this.year = year;
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
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "HallFeeDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", year=" + year +
                ", fee=" + fee +
                '}';
    }
}
