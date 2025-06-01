package com.HMS.hms.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for student information update requests
 * Contains only the fields that students are allowed to update
 */
public class StudentUpdateRequest {
    
    @Size(max = 100, message = "Department name cannot exceed 100 characters")
    private String department;
    
    private Integer batch;
    
    @Size(max = 20, message = "Contact number cannot exceed 20 characters")
    private String contactNo;
    
    @Size(max = 500, message = "Present address cannot exceed 500 characters")
    private String presentAddress;
    
    @Size(max = 500, message = "Permanent address cannot exceed 500 characters")
    private String permanentAddress;
    
    @Pattern(regexp = "^(attached|resident)$", message = "Residency status must be either 'attached' or 'resident'")
    private String residencyStatus;
    
    // Constructors
    public StudentUpdateRequest() {}
    
    public StudentUpdateRequest(String department, Integer batch, String contactNo, 
                               String presentAddress, String permanentAddress, String residencyStatus) {
        this.department = department;
        this.batch = batch;
        this.contactNo = contactNo;
        this.presentAddress = presentAddress;
        this.permanentAddress = permanentAddress;
        this.residencyStatus = residencyStatus;
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public Integer getBatch() {
        return batch;
    }
    
    public void setBatch(Integer batch) {
        this.batch = batch;
    }
    
    public String getContactNo() {
        return contactNo;
    }
    
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    
    public String getPresentAddress() {
        return presentAddress;
    }
    
    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }
    
    public String getPermanentAddress() {
        return permanentAddress;
    }
    
    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }
    
    public String getResidencyStatus() {
        return residencyStatus;
    }
    
    public void setResidencyStatus(String residencyStatus) {
        this.residencyStatus = residencyStatus;
    }
    
    @Override
    public String toString() {
        return "StudentUpdateRequest{" +
                "department='" + department + '\'' +
                ", batch=" + batch +
                ", contactNo='" + contactNo + '\'' +
                ", presentAddress='" + presentAddress + '\'' +
                ", permanentAddress='" + permanentAddress + '\'' +
                ", residencyStatus='" + residencyStatus + '\'' +
                '}';
    }
}
