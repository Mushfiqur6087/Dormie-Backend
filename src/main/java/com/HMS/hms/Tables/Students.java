package com.HMS.hms.Tables;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Students {

    @Id
    private Long userId; // Primary key of students, also foreign key

    @OneToOne
    @MapsId // Use user.userId as this entity's ID
    @JoinColumn(name = "user_id") // Foreign key to users.userId
    private Users user;

    @Column(name = "student_id", unique = true, nullable = false)
    private Long studentId; // Unique student identifier (e.g., roll number)

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "reg_no", unique = true, nullable = true)
    private String regNo;

    private String department;

    private Integer batch;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "present_address")
    private String presentAddress;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "residency_status", nullable = false)
    private String residencyStatus = "attached";

    // Getters and Setters

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
