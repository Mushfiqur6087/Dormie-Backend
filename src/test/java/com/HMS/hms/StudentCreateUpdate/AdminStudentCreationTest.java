package com.HMS.hms.StudentCreateUpdate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.HMS.hms.Service.StudentsService;
import com.HMS.hms.Service.UserService;
import com.HMS.hms.Tables.Students;
import com.HMS.hms.Tables.Users;

/**
 * Tests admin student creation functionality.
 * 
 * <p>Validates the complete workflow of admin creating students via /admin/signup,
 * including dual entity creation, name parsing, and default value assignment.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class AdminStudentCreationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentsService studentsService;

    private String authBaseUrl;
    private String adminJwtToken;

    /**
     * Test that verifies admin can successfully authenticate using default credentials.
     * 
     * <p>Validates admin bootstrap configuration and ensures default admin user
     * can authenticate to obtain a valid JWT token with ROLE_ADMIN.</p>
     */
    @Test
    void testAdminCanLogin() {
        // Setup URLs
        setupUrls();
        
        // Login with default admin credentials
        loginAsAdmin();
        
        // Verify admin login was successful
        assertNotNull(adminJwtToken, "Admin should be able to login with default credentials");
    }

    /**
     * Test that verifies admin can create a student and both User and Student entities are created.
     * 
     * <p>Validates the dual entity creation functionality of /admin/signup endpoint.
     * Ensures both authentication and profile records are properly linked.</p>
     */
    @Test
    void testAdminCanCreateSingleStudent() {
        // Setup URLs and login
        setupUrls();
        loginAsAdmin();
        
        // Create one student and verify both User and Student entities are created
        SignupRequest studentRequest = createStudentSignupRequest(1);
        createAndVerifyStudent(studentRequest);
    }

    /**
     * Test that verifies username parsing when only a single word is provided.
     * 
     * <p>Validates that single word username ("John") is parsed correctly:
     * firstName = "John", lastName = "NONE".</p>
     */
    @Test
    void testStudentNameParsingWithSingleWord() {
        // Setup URLs and login
        setupUrls();
        loginAsAdmin();
        
        // Test single word username parsing
        SignupRequest studentRequest = new SignupRequest();
        studentRequest.setUsername("John");
        studentRequest.setEmail("john@dormie.com");
        studentRequest.setPassword("Student123!");
        studentRequest.setRole("STUDENT");
        studentRequest.setStudentId(20240010L);
        
        createAndVerifyStudent(studentRequest);
        
        // Verify name parsing for single word
        Optional<Students> studentOpt = studentsService.findByStudentId(20240010L);
        assertTrue(studentOpt.isPresent());
        Students student = studentOpt.get();
        assertEquals("John", student.getFirstName());
        assertEquals("NONE", student.getLastName());
    }

    /**
     * Test that verifies username parsing when multiple words are provided.
     * 
     * <p>Validates that multi-word username ("John Doe Smith") is parsed correctly:
     * firstName = "John", lastName = "Doe Smith".</p>
     */
    @Test
    void testStudentNameParsingWithMultipleWords() {
        // Setup URLs and login
        setupUrls();
        loginAsAdmin();
        
        // Test multiple word username parsing
        SignupRequest studentRequest = new SignupRequest();
        studentRequest.setUsername("John Doe Smith");
        studentRequest.setEmail("johndoesmith@dormie.com");
        studentRequest.setPassword("Student123!");
        studentRequest.setRole("STUDENT");
        studentRequest.setStudentId(20240011L);
        
        createAndVerifyStudent(studentRequest);
        
        // Verify name parsing for multiple words
        Optional<Students> studentOpt = studentsService.findByStudentId(20240011L);
        assertTrue(studentOpt.isPresent());
        Students student = studentOpt.get();
        assertEquals("John", student.getFirstName());
        assertEquals("Doe Smith", student.getLastName());
    }

    /**
     * Test that verifies all default values are correctly assigned to student entities.
     * 
     * <p>Validates that when a student is created via /admin/signup, all required
     * default values are automatically assigned to the student profile.</p>
     */
    @Test
    void testStudentDefaultValuesAreSet() {
        // Setup URLs and login
        setupUrls();
        loginAsAdmin();
        
        // Create student and verify default values
        SignupRequest studentRequest = createStudentSignupRequest(2);
        createAndVerifyStudent(studentRequest);
        
        // Verify all default values are correctly set
        Optional<Students> studentOpt = studentsService.findByStudentId(studentRequest.getStudentId());
        assertTrue(studentOpt.isPresent());
        Students student = studentOpt.get();
        
        assertEquals("attached", student.getResidencyStatus(), "Default residency status should be 'attached'");
        assertEquals("Not Specified", student.getDepartment(), "Default department should be 'Not Specified'");
        assertEquals(2025, student.getBatch(), "Default batch should be 2025");
        assertEquals("Not Provided", student.getContactNo(), "Default contact number should be 'Not Provided'");
        assertEquals("Not Provided", student.getPresentAddress(), "Default present address should be 'Not Provided'");
        assertEquals("Not Provided", student.getPermanentAddress(), "Default permanent address should be 'Not Provided'");
    }

    /**
     * Sets up the base URLs for authentication endpoints.
     */
    private void setupUrls() {
        authBaseUrl = "http://localhost:" + port + "/api/auth";
    }

    /**
     * Authenticates as admin and obtains JWT token for authorized operations.
     * 
     * <p>Performs admin login using default credentials and stores the JWT token
     * for use in subsequent admin operations.</p>
     */
    private void loginAsAdmin() {
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
        if (jwtResponse != null) {
            assertNotNull(jwtResponse.getAccessToken(), "JWT token should not be null");
            assertTrue(jwtResponse.getRoles().contains("ROLE_ADMIN"), "User should have ADMIN role");
            adminJwtToken = jwtResponse.getAccessToken();
        }
    }

    /**
     * Creates a new student via /admin/signup endpoint and verifies the creation.
     * 
     * <p>Makes authenticated POST request to /admin/signup, verifies HTTP response,
     * and calls database verification to ensure both User and Student entities
     * were created correctly.</p>
     * 
     * @param studentSignupRequest the signup request containing student data
     */
    private void createAndVerifyStudent(SignupRequest studentSignupRequest) {
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(adminJwtToken);
        HttpEntity<SignupRequest> signupRequest = new HttpEntity<>(studentSignupRequest, authHeaders);

        ResponseEntity<MessageResponse> signupResponse = restTemplate.exchange(
            authBaseUrl + "/admin/signup", HttpMethod.POST, signupRequest, MessageResponse.class);

        assertEquals(HttpStatus.OK, signupResponse.getStatusCode(), 
            "Student creation should be successful");
        
        MessageResponse signupResponseBody = signupResponse.getBody();
        assertNotNull(signupResponseBody, "Signup response should not be null");
        if (signupResponseBody != null) {
            assertEquals("Student user registered successfully!", signupResponseBody.getMessage(),
                "Should receive success message");
        }

        // Verify that the User and Student entities are created
        verifyUserAndStudentCreated(studentSignupRequest);
    }

    /**
     * Verifies that both User and Student entities were created correctly in the database.
     * 
     * <p>Performs comprehensive validation of the dual entity creation process by checking
     * both User and Student records, their relationships, name parsing, and default values.</p>
     * 
     * @param studentSignupRequest the original signup request to validate against
     */
    private void verifyUserAndStudentCreated(SignupRequest studentSignupRequest) {
        // Check if User entity is created
        Optional<Users> userOptional = userService.findByEmail(studentSignupRequest.getEmail());
        assertTrue(userOptional.isPresent(), "User should be created in the system");
        
        Users user = userOptional.get();
        assertEquals(studentSignupRequest.getEmail(), user.getEmail(), "User email should match");
        assertEquals("STUDENT", user.getRole(), "User should have STUDENT role");

        // Check if Student entity is created
        Optional<Students> studentOptional = studentsService.findByStudentId(studentSignupRequest.getStudentId());
        assertTrue(studentOptional.isPresent(), "Student should be created in the system");
        
        Students student = studentOptional.get();
        assertEquals(studentSignupRequest.getStudentId(), student.getStudentId(), "Student ID should match");
        
        // Verify the entities are properly linked
        assertEquals(user.getUserId(), student.getUserId(), "Student should be linked to the User entity");
        
        // Verify student name parsing from username
        String username = studentSignupRequest.getUsername();
        String[] nameParts = username.trim().split("\\s+");
        if (nameParts.length == 1) {
            assertEquals(nameParts[0], student.getFirstName(), "First name should be parsed from username");
            assertEquals("NONE", student.getLastName(), "Last name should be 'NONE' for single word username");
        } else {
            assertEquals(nameParts[0], student.getFirstName(), "First name should be parsed from username");
            // For multiple words, join all parts after first as last name
            StringBuilder expectedLastName = new StringBuilder();
            for (int i = 1; i < nameParts.length; i++) {
                if (i > 1) expectedLastName.append(" ");
                expectedLastName.append(nameParts[i]);
            }
            assertEquals(expectedLastName.toString(), student.getLastName(), "Last name should be parsed from username");
        }
        
        // Verify default values are set correctly
        assertEquals("attached", student.getResidencyStatus(), "Default residency status should be 'attached'");
        assertEquals("Not Specified", student.getDepartment(), "Default department should be 'Not Specified'");
        assertEquals(2025, student.getBatch(), "Default batch should be 2025");
        assertEquals("Not Provided", student.getContactNo(), "Default contact number should be 'Not Provided'");
        assertEquals("Not Provided", student.getPresentAddress(), "Default present address should be 'Not Provided'");
        assertEquals("Not Provided", student.getPermanentAddress(), "Default permanent address should be 'Not Provided'");
    }

    /**
     * Creates a test SignupRequest with sequential student data.
     * 
     * <p>Generates a SignupRequest with unique student data using the provided number
     * to ensure no conflicts with existing test data.</p>
     * 
     * @param studentNumber sequential number to create unique student data
     * @return configured SignupRequest ready for testing
     */
    private SignupRequest createStudentSignupRequest(int studentNumber) {
        SignupRequest request = new SignupRequest();
        request.setUsername("student" + studentNumber);
        request.setEmail("student" + studentNumber + "@dormie.com");
        request.setPassword("Student123!");
        request.setRole("STUDENT");
        request.setStudentId((long) (20240000 + studentNumber));
        return request;
    }
}
