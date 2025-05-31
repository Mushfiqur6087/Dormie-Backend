package com.HMS.hms;

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
import com.HMS.hms.Service.UserService;

/**
 * Tests AdminBootstrapConfig functionality.
 * 
 * <p>Verifies that the admin user is created during startup and can authenticate
 * to get a valid JWT token with admin privileges.</p>
 * 
 * @see com.HMS.hms.config.AdminBootstrapConfig
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class AdminBootstrapConfigTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    /**
     * Test that verifies admin user bootstrap and successful authentication.
     * 
     * <p>Validates that AdminBootstrapConfig creates a default admin user and that
     * this admin can authenticate to obtain a valid JWT token with ROLE_ADMIN.</p>
     * 
     * @see com.HMS.hms.config.AdminBootstrapConfig
     * @see com.HMS.hms.Controller.AuthController#authenticateUser
     */
    @Test
    void testAdminBootstrapAndLogin() throws Exception {
        // Verify that admin user was created by AdminBootstrapConfig
        assertTrue(userService.adminExists(), "Admin user should exist after bootstrap");
        
        // Test admin login with default credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@dormie.com");
        loginRequest.setPassword("Admin123!");

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // Perform login request
        String url = "http://localhost:" + port + "/api/auth/signin";
        ResponseEntity<JwtResponse> response = restTemplate.exchange(
            url, HttpMethod.POST, request, JwtResponse.class);

        // Verify successful login
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getAccessToken());
        assertEquals("admin@dormie.com", jwtResponse.getEmail());
        assertTrue(jwtResponse.getRoles().contains("ROLE_ADMIN"));
    }
}
