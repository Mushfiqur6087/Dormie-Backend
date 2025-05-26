package com.HMS.hms.Tables;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dining_fee")
public class DiningFee {

    // Enum for residency types
    public enum ResidencyType {
        ATTACHED("attached"),
        RESIDENT("resident");

        private final String value;

        ResidencyType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ResidencyType fromString(String value) {
            for (ResidencyType type : ResidencyType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid residency type: " + value + ". Must be 'attached' or 'resident'.");
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
    private ResidencyType type; // attached/resident

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    // Default constructor
    public DiningFee() {}

    // Constructor with parameters
    public DiningFee(ResidencyType type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.type = type;
        this.year = year;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
    }

    // Constructor with string type (for convenience)
    public DiningFee(String type, Integer year, LocalDate startDate, LocalDate endDate, BigDecimal fee) {
        this.type = ResidencyType.fromString(type);
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
        this.type = type;
    }

    public void setType(String type) {
        this.type = ResidencyType.fromString(type);
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
