package com.HMS.hms.Tables;

import jakarta.persistence.*;

@Entity
@Table(name = "student_payment_info")
public class StudentPaymentInfo {

    // Enum for fee type
    public enum FeeType {
        HALL("hall"),
        DINING("dining");

        private final String value;

        FeeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static FeeType fromString(String value) {
            for (FeeType type : FeeType.values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid fee type: " + value + ". Must be 'hall' or 'dining'.");
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;

    @Column(name = "fee_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Column(name = "tran_id")
    private String tranId;

    @Column(name = "val_id")
    private String valId;

    @Column(name = "payment_method")
    private String paymentMethod;

    // Default constructor
    public StudentPaymentInfo() {}

    // Constructor with parameters
    public StudentPaymentInfo(FeeType feeType, String tranId, String valId, String paymentMethod) {
        this.feeType = feeType;
        this.tranId = tranId;
        this.valId = valId;
        this.paymentMethod = paymentMethod;
    }

    // Constructor with string values for convenience
    public StudentPaymentInfo(String feeType, String tranId, String valId, String paymentMethod) {
        this.feeType = FeeType.fromString(feeType);
        this.tranId = tranId;
        this.valId = valId;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = FeeType.fromString(feeType);
    }

    public String getFeeTypeAsString() {
        return feeType != null ? feeType.getValue() : null;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getValId() {
        return valId;
    }

    public void setValId(String valId) {
        this.valId = valId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "StudentPaymentInfo{" +
                "feeId=" + feeId +
                ", feeType=" + feeType +
                ", tranId='" + tranId + '\'' +
                ", valId='" + valId + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
