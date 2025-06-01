package com.HMS.hms.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.HMS.hms.DTO.JwtResponse;
import com.HMS.hms.DTO.LoginRequest;
import com.HMS.hms.DTO.MessageResponse;
import com.HMS.hms.DTO.SignupRequest;

/**
 * Utility class for common test operations.
 * 
 * <p>Provides reusable methods for authentication, student creation, 
 * and other common test scenarios across different test classes.</p>
 */
@SuppressWarnings("unused")
public class TestUtility {

    private final TestRestTemplate restTemplate;
    private final int port;
    private final String authBaseUrl;
    private final String studentsBaseUrl;

    /**
     * Constructor to initialize the test utility with required dependencies.
     * 
     * @param restTemplate The TestRestTemplate for making HTTP requests
     * @param port The server port for the test
     */
    public TestUtility(TestRestTemplate restTemplate, int port) {
        this.restTemplate = restTemplate;
        this.port = port;
        this.authBaseUrl = "http://localhost:" + port + "/api/auth";
        this.studentsBaseUrl = "http://localhost:" + port + "/api/students";
    }

    /**
     * Authenticates as admin and returns the JWT token.
     * 
     * @return JWT token for admin authentication
     */
    @SuppressWarnings("null")
    public String loginAsAdmin() {
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
        
        String accessToken = jwtResponse.getAccessToken();
        assertNotNull(accessToken, "JWT token should not be null");
        
        return accessToken;
    }

    /**
     * Authenticates as a student and returns the JWT token.
     * 
     * @param email Student's email
     * @param password Student's password
     * @return JWT token for student authentication
     */
    @SuppressWarnings("null")
    public String loginAsStudent(String email, String password) {
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
        
        String accessToken = jwtResponse.getAccessToken();
        assertNotNull(accessToken, "JWT token should not be null");
        
        return accessToken;
    }

    /**
     * Creates a student via admin signup endpoint.
     * 
     * @param adminJwtToken JWT token for admin authentication
     * @param studentRequest SignupRequest containing student details
     */
    @SuppressWarnings("null")
    public void createStudent(String adminJwtToken, SignupRequest studentRequest) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(adminJwtToken);
        HttpEntity<SignupRequest> signupRequest = new HttpEntity<>(studentRequest, authHeaders);

        ResponseEntity<MessageResponse> signupResponse = restTemplate.exchange(
            authBaseUrl + "/admin/signup", HttpMethod.POST, signupRequest, MessageResponse.class);

        assertEquals(HttpStatus.OK, signupResponse.getStatusCode(), "Student creation should be successful");
        
        MessageResponse signupResponseBody = signupResponse.getBody();
        assertNotNull(signupResponseBody, "Signup response should not be null");
        
        String responseMessage = signupResponseBody.getMessage();
        assertEquals("Student user registered successfully!", responseMessage,
            "Should receive success message");
    }

    /**
     * Creates a default test SignupRequest for student creation.
     * 
     * @return SignupRequest with default test data
     */
    public SignupRequest createDefaultStudentSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("Test Student");
        request.setEmail("teststudent@dormie.com");
        request.setPassword("TestPassword123!");
        request.setRole("STUDENT");
        request.setStudentId(20240999L);
        return request;
    }

    /**
     * Creates a custom test SignupRequest for student creation.
     * 
     * @param username Student's username
     * @param email Student's email
     * @param password Student's password
     * @param studentId Student's ID
     * @return SignupRequest with custom data
     */
    public SignupRequest createCustomStudentSignupRequest(String username, String email, 
                                                         String password, Long studentId) {
        SignupRequest request = new SignupRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole("STUDENT");
        request.setStudentId(studentId);
        return request;
    }

    /**
     * Performs the complete student creation workflow.
     * Creates a student via admin and returns the student's credentials.
     * 
     * @return StudentCredentials containing email and password for the created student
     */
    public StudentCredentials createStudentWithCredentials() {
        String adminJwtToken = loginAsAdmin();
        SignupRequest studentRequest = createDefaultStudentSignupRequest();
        createStudent(adminJwtToken, studentRequest);
        
        return new StudentCredentials(studentRequest.getEmail(), studentRequest.getPassword());
    }

    /**
     * Performs the complete student creation workflow with custom data.
     * Creates a student via admin and returns the student's credentials.
     * 
     * @param username Student's username
     * @param email Student's email
     * @param password Student's password
     * @param studentId Student's ID
     * @return StudentCredentials containing email and password for the created student
     */
    public StudentCredentials createCustomStudentWithCredentials(String username, String email, 
                                                               String password, Long studentId) {
        String adminJwtToken = loginAsAdmin();
        SignupRequest studentRequest = createCustomStudentSignupRequest(username, email, password, studentId);
        createStudent(adminJwtToken, studentRequest);
        
        return new StudentCredentials(email, password);
    }

    /**
     * Gets the base URL for authentication endpoints.
     * 
     * @return Authentication base URL
     */
    public String getAuthBaseUrl() {
        return authBaseUrl;
    }

    /**
     * Gets the base URL for students endpoints.
     * 
     * @return Students base URL
     */
    public String getStudentsBaseUrl() {
        return studentsBaseUrl;
    }

    /**
     * Simple data class to hold student credentials.
     */
    public static class StudentCredentials {
        private final String email;
        private final String password;

        public StudentCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}
