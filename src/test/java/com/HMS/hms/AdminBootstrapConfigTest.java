// package com.HMS.hms;

// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import com.HMS.hms.Service.UserService;
// import com.HMS.hms.utility.TestUtility;

// /**
//  * Tests AdminBootstrapConfig functionality.
//  * 
//  * <p>Verifies that the admin user is created during startup and can authenticate
//  * to get a valid JWT token with admin privileges.</p>
//  * 
//  * @see com.HMS.hms.config.AdminBootstrapConfig
//  */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
// @Transactional
// class AdminBootstrapConfigTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Autowired
//     private UserService userService;

//     private TestUtility testUtility;

//     /**
//      * Test that verifies admin user bootstrap and successful authentication.
//      * 
//      * <p>Validates that AdminBootstrapConfig creates a default admin user and that
//      * this admin can authenticate to obtain a valid JWT token with ROLE_ADMIN.</p>
//      * 
//      * @see com.HMS.hms.config.AdminBootstrapConfig
//      * @see com.HMS.hms.Controller.AuthController#authenticateUser
//      */
//     @Test
//     void testAdminBootstrapAndLogin() throws Exception {
//         // Initialize test utility
//         testUtility = new TestUtility(restTemplate, port);
        
//         // Verify that admin user was created by AdminBootstrapConfig
//         assertTrue(userService.adminExists(), "Admin user should exist after bootstrap");
        
//         // Test admin login using TestUtility - this validates both bootstrap and authentication
//         String adminJwtToken = testUtility.loginAsAdmin();
        
//         // Verify successful login (loginAsAdmin method already performs assertions)
//         assertNotNull(adminJwtToken, "Admin JWT token should not be null");
//     }
// }
