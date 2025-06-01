package com.HMS.hms.Tables;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dining_fee")
public class DiningFee {

    // Enum for residency types - dining fees are only for residents
    public enum ResidencyType {
        RESIDENT("resident");

        private final String value;

        ResidencyType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ResidencyType fromString(String value) {
            if ("resident".equalsIgnoreCase(value)) {
                return RESIDENT;
            }
            throw new IllegalArgumentException("Invalid residency type: " + value + ". Dining fees are only for 'resident' students.");
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResidencyType type = ResidencyType.RESIDENT; // Always resident for dining fees

    @Column(name = "`year`", nullable = false)
    private Integer year;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    // Default constructor
    public DiningFee() {}

    // Constructor with parameters - type is always RESIDENT for dining fees
    public DiningFee(ResidencyType type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.type = ResidencyType.RESIDENT; // Always resident for dining fees
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    // Constructor with string type (for convenience) - type is always RESIDENT for dining fees
    public DiningFee(String type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.type = ResidencyType.RESIDENT; // Always resident for dining fees
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

    public ResidencyType getType() {
        return type;
    }

    public void setType(ResidencyType type) {
        this.type = ResidencyType.RESIDENT; // Always resident for dining fees
    }

    public void setType(String type) {
        this.type = ResidencyType.RESIDENT; // Always resident for dining fees
    }

    public String getTypeAsString() {
        return type != null ? type.getValue() : null;
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
        return "DiningFee{" +
                "id=" + id +
                ", type=" + type +
                ", year=" + year +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", fee=" + fee +
                '}';
    }
}
