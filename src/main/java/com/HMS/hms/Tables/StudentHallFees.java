package com.HMS.hms.Tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_hall_fees")
public class StudentHallFees {

    // Enum for payment status
    public enum PaymentStatus {
        PAID("paid"),
        UNPAID("unpaid");

        private final String value;

        PaymentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static PaymentStatus fromString(String value) {
            for (PaymentStatus status : PaymentStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid payment status: " + value + ". Must be 'paid' or 'unpaid'.");
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;

    @Column(name = "user_id")
    private Long userId;  // Foreign key to users.userId

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_type", nullable = false)
    private String studentType; // "Attached" or "Resident"

    @Column(name = "`year`", nullable = true)
    private Integer year; // e.g. 2024

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // paid/unpaid

    // Default constructor
    public StudentHallFees() {}

    // Constructor with parameters
    public StudentHallFees(Long userId, Long studentId, String studentType, Integer year, PaymentStatus status) {
        this.userId = userId;
        this.studentId = studentId;
        this.studentType = studentType;
        this.year = year;
        this.status = status;
    }

    // Constructor with string status (for convenience)
    public StudentHallFees(Long userId, Long studentId, String studentType, Integer year, String status) {
        this.userId = userId;
        this.studentId = studentId;
        this.studentType = studentType;
        this.year = year;
        this.status = PaymentStatus.fromString(status);
    }

    // Getters and Setters
    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = PaymentStatus.fromString(status);
    }

    public String getStatusAsString() {
        return status != null ? status.getValue() : null;
    }

    @Override
    public String toString() {
        return "StudentHallFees{" +
                "feeId=" + feeId +
                ", userId=" + userId +
                ", studentId=" + studentId +
                ", studentType='" + studentType + '\'' +
                ", year=" + year +
                ", status=" + status +
                '}';
    }
}
