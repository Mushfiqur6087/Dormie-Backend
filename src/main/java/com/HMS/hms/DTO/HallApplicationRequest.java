package com.HMS.hms.DTO;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

// Consider adding Lombok's @Data annotation for getters/setters/constructors if you use it
// import lombok.Data;
// @Data
public class HallApplicationRequest {

    // These field names MUST match the 'formData.append' keys from frontend
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "College name is required")
    private String college;

    @NotBlank(message = "College location is required")
    private String collegeLocation;

    @NotNull(message = "Family income is required")
    @Min(value = 0, message = "Family income cannot be negative")
    private BigDecimal familyIncome;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Postcode is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Postcode must be 4 digits")
    private String postcode;

    @NotNull(message = "Student image is required")
    private MultipartFile studentImage; // Spring automatically binds the file here

    @NotBlank(message = "Local relative option is required")
    @Pattern(regexp = "^(yes|no)$", message = "Value must be 'yes' or 'no'")
    private String hasLocalRelative; // "yes" or "no" (String from frontend)

    // Not @NotBlank here because it's conditional
    private String localRelativeAddress;

    // --- Getters and Setters (if not using Lombok) ---
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
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
    public MultipartFile getStudentImage() { return studentImage; }
    public void setStudentImage(MultipartFile studentImage) { this.studentImage = studentImage; }
    public String getHasLocalRelative() { return hasLocalRelative; }
    public void setHasLocalRelative(String hasLocalRelative) { this.hasLocalRelative = hasLocalRelative; }
    public String getLocalRelativeAddress() { return localRelativeAddress; }
    public void setLocalRelativeAddress(String localRelativeAddress) { this.localRelativeAddress = localRelativeAddress; }
}