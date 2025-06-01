package com.HMS.hms.StudentCreateUpdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.HMS.hms.DTO.JwtResponse;
import com.HMS.hms.DTO.LoginRequest;
import com.HMS.hms.DTO.MessageResponse;
import com.HMS.hms.DTO.SignupRequest;
import com.HMS.hms.DTO.StudentDTO;
import com.HMS.hms.DTO.StudentUpdateRequest;

/**
 * Integration test for student update functionality.
 * 
 * <p>Tests the complete workflow where admin creates students and then 
 * students update their own information using the StudentsController.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class StudentUpdateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String authBaseUrl;
    private String studentsBaseUrl;

    /**
     * Main test that verifies the complete student update workflow.
     * 
     * <p>Creates a student via admin, then logs in as that student and updates their information.</p>
     */
    @Test
    void testStudentCanUpdateOwnInformation() {
        // Setup URLs
        setupUrls();
        
        // Step 1: Login as admin and get JWT token
        String adminJwtToken = loginAsAdmin();
        
        // Step 2: Admin creates a new student
        SignupRequest studentRequest = createStudentSignupRequest();
        createStudent(adminJwtToken, studentRequest);
        
        // Step 3: Login as the newly created student
        String studentJwtToken = loginAsStudent(studentRequest.getEmail(), studentRequest.getPassword());
        
        // Step 4: Student updates their own information
        StudentUpdateRequest updateRequest = createStudentUpdateRequest();
        updateStudentInformation(studentJwtToken, updateRequest);
    }

    /**
     * Sets up the base URLs for API endpoints.
     */
    private void setupUrls() {
        authBaseUrl = "http://localhost:" + port + "/api/auth";
        studentsBaseUrl = "http://localhost:" + port + "/api/students";
    }

    /**
     * Authenticates as admin and returns the JWT token.
     */
    private String loginAsAdmin() {
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@dormie.com");
        adminLogin.setPassword("Admin123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(adminLogin, headers);

        ResponseEntity<JwtResponse> response = restTemplate.exchange(
            authBaseUrl + "/signin", HttpMethod.POST, request, JwtResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Admin login should be successful");
        
        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse, "JWT response should not be null");
        assertNotNull(jwtResponse.getAccessToken(), "JWT token should not be null");
        
        return jwtResponse.getAccessToken();
    }

    /**
     * Authenticates as a student and returns the JWT token.
     */
    private String loginAsStudent(String email, String password) {
        LoginRequest studentLogin = new LoginRequest();
        studentLogin.setEmail(email);
        studentLogin.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(studentLogin, headers);

        ResponseEntity<JwtResponse> response = restTemplate.exchange(
            authBaseUrl + "/signin", HttpMethod.POST, request, JwtResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Student login should be successful");
        
        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse, "JWT response should not be null");
        assertNotNull(jwtResponse.getAccessToken(), "JWT token should not be null");
        
        return jwtResponse.getAccessToken();
    }

    /**
     * Creates a student via admin signup endpoint.
     */
    private void createStudent(String adminJwtToken, SignupRequest studentRequest) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(adminJwtToken);
        HttpEntity<SignupRequest> signupRequest = new HttpEntity<>(studentRequest, authHeaders);

        ResponseEntity<MessageResponse> signupResponse = restTemplate.exchange(
            authBaseUrl + "/admin/signup", HttpMethod.POST, signupRequest, MessageResponse.class);

        assertEquals(HttpStatus.OK, signupResponse.getStatusCode(), "Student creation should be successful");
        
        MessageResponse signupResponseBody = signupResponse.getBody();
        assertNotNull(signupResponseBody, "Signup response should not be null");
        assertEquals("Student user registered successfully!", signupResponseBody.getMessage(),
            "Should receive success message");
    }

    /**
     * Updates student information using the student's JWT token.
     */
    private void updateStudentInformation(String studentToken, StudentUpdateRequest updateRequest) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(studentToken);
        HttpEntity<StudentUpdateRequest> request = new HttpEntity<>(updateRequest, authHeaders);

        ResponseEntity<StudentDTO> response = restTemplate.exchange(
            studentsBaseUrl + "/update", HttpMethod.PUT, request, StudentDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Student information update should be successful");
        
        StudentDTO updatedStudent = response.getBody();
        assertNotNull(updatedStudent, "Updated student information should not be null");
        
        // Verify the update was successful by checking key fields
        assertEquals(updateRequest.getDepartment(), updatedStudent.getDepartment(), 
            "Department should be updated");
        assertEquals(updateRequest.getBatch(), updatedStudent.getBatch(), 
            "Batch should be updated");
        assertEquals(updateRequest.getContactNo(), updatedStudent.getContactNo(), 
            "Contact number should be updated");
        assertEquals(updateRequest.getPresentAddress(), updatedStudent.getPresentAddress(), 
            "Present address should be updated");
        assertEquals(updateRequest.getPermanentAddress(), updatedStudent.getPermanentAddress(), 
            "Permanent address should be updated");
        assertEquals(updateRequest.getResidencyStatus(), updatedStudent.getResidencyStatus(), 
            "Residency status should be updated");
    }

    /**
     * Creates a test SignupRequest for student creation.
     */
    private SignupRequest createStudentSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("Test Student");
        request.setEmail("teststudent@dormie.com");
        request.setPassword("TestPassword123!");
        request.setRole("STUDENT");
        request.setStudentId(20240999L);
        return request;
    }

    /**
     * Creates a StudentUpdateRequest with test data.
     */
    private StudentUpdateRequest createStudentUpdateRequest() {
        StudentUpdateRequest updateRequest = new StudentUpdateRequest();
        updateRequest.setDepartment("Computer Science");
        updateRequest.setBatch(2024);
        updateRequest.setContactNo("01712345678");
        updateRequest.setPresentAddress("123 University Campus, Dhaka");
        updateRequest.setPermanentAddress("456 Home Village, Chittagong");
        updateRequest.setResidencyStatus("resident");
        return updateRequest;
    }
}
