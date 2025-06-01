package com.HMS.hms.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class StudentHallFeeDTO {
    
    private Long feeId;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotBlank(message = "Student type is required")
    @Pattern(regexp = "^(Resident|Attached|resident|attached)$", message = "Student type must be 'Resident', 'Attached', 'resident', or 'attached'")
    private String studentType;
    
    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2030, message = "Year must be 2030 or earlier")
    private Integer year;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(PAID|UNPAID|paid|unpaid)$", message = "Status must be 'PAID', 'UNPAID', 'paid', or 'unpaid'")
    private String status;

    // Default constructor
    public StudentHallFeeDTO() {}

    // Constructor without feeId (for creation)
    public StudentHallFeeDTO(Long studentId, String studentType, Integer year, String status) {
        this.studentId = studentId;
        this.studentType = studentType;
        this.year = year;
        this.status = status;
    }

    // Constructor with feeId (for responses)
    public StudentHallFeeDTO(Long feeId, Long studentId, String studentType, Integer year, String status) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.studentType = studentType;
        this.year = year;
        this.status = status;
    }

    // Getters and Setters
    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StudentHallFeeDTO{" +
                "feeId=" + feeId +
                ", studentId=" + studentId +
                ", studentType='" + studentType + '\'' +
                ", year=" + year +
                ", status='" + status + '\'' +
                '}';
    }
}
