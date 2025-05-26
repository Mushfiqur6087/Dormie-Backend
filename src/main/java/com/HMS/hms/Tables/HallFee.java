package com.HMS.hms.Tables;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hall_fee")
public class HallFee {

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

    @Column(name = "fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal fee;

    // Default constructor
    public HallFee() {}

    // Constructor with parameters
    public HallFee(ResidencyType type, Integer year, BigDecimal fee) {
        this.type = type;
        this.year = year;
        this.fee = fee;
    }

    // Constructor with string type (for convenience)
    public HallFee(String type, Integer year, BigDecimal fee) {
        this.type = ResidencyType.fromString(type);
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

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "HallFee{" +
                "id=" + id +
                ", type=" + type +
                ", year=" + year +
                ", fee=" + fee +
                '}';
    }
}
