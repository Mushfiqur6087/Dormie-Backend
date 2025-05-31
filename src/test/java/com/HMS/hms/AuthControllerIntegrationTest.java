package com.HMS.hms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.HMS.hms.DTO.JwtResponse;
import com.HMS.hms.DTO.LoginRequest;
import com.HMS.hms.DTO.MessageResponse;
import com.HMS.hms.DTO.SignupRequest;

/**
 * Integration test class for AuthController functionality.
 * 
 * <p>Tests the complete authentication workflow including admin login,
 * student creation, and student authentication.</p>
 * 
 * @see com.HMS.hms.Controller.AuthController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String adminJwtToken;

    /**
     * Sets up test environment before each test method execution.
     * 
     * <p>Configures base URL and authenticates as admin to obtain JWT token
     * for subsequent admin operations.</p>
     */
    @BeforeEach
    @Test
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        
        // Login as admin to get JWT token for admin operations
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@dormie.com");
        adminLogin.setPassword("Admin123!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(adminLogin, headers);

        ResponseEntity<JwtResponse> response = restTemplate.exchange(
            baseUrl + "/signin", HttpMethod.POST, request, JwtResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JwtResponse jwtResponse = response.getBody();
            if (jwtResponse != null) {
                adminJwtToken = jwtResponse.getAccessToken();
            }
        }
    }

    /**
     * Test that verifies the complete admin-to-student workflow.
     * 
     * <p>Tests admin creating a student via /admin/signup and verifies
     * that the newly created student can successfully authenticate.</p>
     * 
     * @see com.HMS.hms.Controller.AuthController#registerStudentUser
     * @see com.HMS.hms.Controller.AuthController#authenticateUser
     */
    @Test
    void testAdminSignupWorkflow_SuccessfulStudentCreationAndLogin() {
        // Verify admin login was successful
        assertNotNull(adminJwtToken, "Admin should be able to login");

        // Test 1: Admin creates a new student user
        SignupRequest newStudentRequest = createStudentSignupRequest();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminJwtToken); // Use admin JWT token
        HttpEntity<SignupRequest> signupRequest = new HttpEntity<>(newStudentRequest, headers);

        ResponseEntity<MessageResponse> signupResponse = restTemplate.exchange(
            baseUrl + "/admin/signup", HttpMethod.POST, signupRequest, MessageResponse.class);

        // Verify student creation was successful
        assertEquals(HttpStatus.OK, signupResponse.getStatusCode());
        MessageResponse signupResponseBody = signupResponse.getBody();
        assertNotNull(signupResponseBody);
        assertEquals("Student user registered successfully!", signupResponseBody.getMessage());

        // Test 2: Newly created student can login
        LoginRequest studentLogin = new LoginRequest();
        studentLogin.setEmail(newStudentRequest.getEmail());
        studentLogin.setPassword(newStudentRequest.getPassword());

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> loginRequest = new HttpEntity<>(studentLogin, loginHeaders);

        ResponseEntity<JwtResponse> loginResponse = restTemplate.exchange(
            baseUrl + "/signin", HttpMethod.POST, loginRequest, JwtResponse.class);

        // Verify student login was successful
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        JwtResponse jwtResponse = loginResponse.getBody();
        assertNotNull(jwtResponse);
        
        assertNotNull(jwtResponse.getAccessToken());
        assertEquals(newStudentRequest.getEmail(), jwtResponse.getEmail());
        assertEquals(newStudentRequest.getUsername(), jwtResponse.getUsername());
        assertTrue(jwtResponse.getRoles().contains("ROLE_STUDENT"));
    }

    /**
     * Creates a test SignupRequest with standardized student data.
     * 
     * @return SignupRequest configured with test student data
     */
    private SignupRequest createStudentSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test.student@example.com");
        request.setPassword("TestPassword123!");
        request.setRole("STUDENT");
        request.setStudentId(12345L);
        return request;
    }
}
