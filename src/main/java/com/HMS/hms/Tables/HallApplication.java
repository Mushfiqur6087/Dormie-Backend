package com.HMS.hms.Tables;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal; // Import for BigDecimal
import java.time.LocalDateTime;

@Entity
@Table(name = "hall_applications")
public class HallApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Users user;

    @Column(name = "student_id_no", nullable = false)
    private Long studentIdNo;

    @Column(nullable = false)
    private String college;

    @Column(name = "college_location", nullable = false)
    private String collegeLocation;

    @Column(name = "family_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal familyIncome;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String postcode;

    @Column(name = "student_image_path", nullable = false)
    private String studentImagePath;

    @Column(name = "has_local_relative", nullable = false)
    private Boolean hasLocalRelative;

    @Column(name = "local_relative_address")
    private String localRelativeAddress;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Column(name = "application_status", nullable = false)
    private String applicationStatus = "PENDING";

    // --- NEW FIELD FOR DISTANCE ---
    @Column(name = "distance_from_hall_km", nullable = true, precision = 10, scale = 2)
    private BigDecimal distanceFromHallKm; // Store as BigDecimal for precision
    // --- END NEW FIELD ---

    public HallApplication() {
        this.applicationDate = LocalDateTime.now();
        this.applicationStatus = "PENDING";
    }

    // Add constructor if needed, or rely on setters after creation
    // ... (existing constructors, getters, setters) ...

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public Long getStudentIdNo() { return studentIdNo; }
    public void setStudentIdNo(Long studentIdNo) { this.studentIdNo = studentIdNo; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getCollegeLocation() { return collegeLocation; }
    public void setCollegeLocation(String collegeLocation) { this.collegeLocation = collegeLocation; }
    public BigDecimal getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(BigDecimal familyIncome) { this.familyIncome = familyIncome; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public String getStudentImagePath() { return studentImagePath; }
    public void setStudentImagePath(String studentImagePath) { this.studentImagePath = studentImagePath; }
    public Boolean getHasLocalRelative() { return hasLocalRelative; }
    public void setHasLocalRelative(Boolean hasLocalRelative) { this.hasLocalRelative = hasLocalRelative; }
    public String getLocalRelativeAddress() { return localRelativeAddress; }
    public void setLocalRelativeAddress(String localRelativeAddress) { this.localRelativeAddress = localRelativeAddress; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    // --- NEW GETTER/SETTER FOR DISTANCE ---
    public BigDecimal getDistanceFromHallKm() {
        return distanceFromHallKm;
    }

    public void setDistanceFromHallKm(BigDecimal distanceFromHallKm) {
        this.distanceFromHallKm = distanceFromHallKm;
    }

    // --- END NEW GETTER/SETTER ---
}