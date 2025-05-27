package com.HMS.hms.DTO;

import java.time.LocalDate;

public class StudentDTO {
    private Long userId;
    private Long studentId;
    private String firstName;
    private String lastName;
    private String regNo;
    private String department;
    private Integer batch;
    private String contactNo;
    private String presentAddress;
    private String permanentAddress;
    private LocalDate dateOfBirth;
    private String residencyStatus;

    // Constructors
    public StudentDTO() {}

    public StudentDTO(Long userId, Long studentId, String firstName, String lastName, String regNo, 
                     String department, Integer batch, String contactNo, String presentAddress, 
                     String permanentAddress, LocalDate dateOfBirth, String residencyStatus) {
        this.userId = userId;
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.regNo = regNo;
        this.department = department;
        this.batch = batch;
        this.contactNo = contactNo;
        this.presentAddress = presentAddress;
        this.permanentAddress = permanentAddress;
        this.dateOfBirth = dateOfBirth;
        this.residencyStatus = residencyStatus;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getResidencyStatus() {
        return residencyStatus;
    }

    public void setResidencyStatus(String residencyStatus) {
        this.residencyStatus = residencyStatus;
    }
}
