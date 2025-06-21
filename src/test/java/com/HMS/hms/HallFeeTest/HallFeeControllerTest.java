// package com.HMS.hms.HallFeeTest;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.math.BigDecimal;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Order;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import com.HMS.hms.DTO.HallFeeDTO;
// import com.HMS.hms.utility.TestUtility;

// /**
//  * Comprehensive integration tests for HallFeeController.
//  * 
//  * <p>Tests all CRUD operations and business logic for hall fee management,
//  * including admin authentication, fee creation, retrieval, updates, and deletion.</p>
//  */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
// @Transactional
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// class HallFeeControllerTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     private TestUtility testUtility;
//     private String adminJwtToken;
//     private String hallFeeBaseUrl;
    
//     // Test data
//     private HallFeeDTO residentFee2024;
//     private HallFeeDTO attachedFee2024;
//     private Long residentFee2024Id;
//     private Long attachedFee2024Id;

//     @BeforeEach
//     public void setUp() {
//         testUtility = new TestUtility(restTemplate, port);
//         adminJwtToken = testUtility.loginAsAdmin();
//         hallFeeBaseUrl = "http://localhost:" + port + "/api/hall-fees";
        
//         // Initialize test data
//         residentFee2024 = new HallFeeDTO("resident", 2024, new BigDecimal("15000.00"));
//         attachedFee2024 = new HallFeeDTO("attached", 2024, new BigDecimal("12000.00"));
//     }

//     /**
//      * Test 1: Admin authentication
//      */
//     @Test
//     @Order(1)
//     void testAdminCanAuthenticate() {
//         assertNotNull(adminJwtToken, "Admin should be able to login and get JWT token");
//         assertTrue(adminJwtToken.length() > 0, "JWT token should not be empty");
//     }

//     /**
//      * Test 2: Create first hall fee (resident 2024)
//      */
//     @Test
//     @Order(2)
//     void testCreateResidentHallFee() {
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<HallFeeDTO> request = new HttpEntity<>(residentFee2024, headers);

//         ResponseEntity<HallFeeDTO> response = restTemplate.exchange(
//             hallFeeBaseUrl, HttpMethod.POST, request, HallFeeDTO.class);

//         assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
//             "Creating resident hall fee should return CREATED status");
        
//         HallFeeDTO createdFee = response.getBody();
//         assertNotNull(createdFee, "Created hall fee should not be null");
//         if (createdFee != null) {
//             assertNotNull(createdFee.getId(), "Created hall fee should have an ID");
//             assertEquals("resident", createdFee.getType(), "Hall fee type should be resident");
//             assertEquals(Integer.valueOf(2024), createdFee.getYear(), "Hall fee year should be 2024");
//             assertEquals(new BigDecimal("15000.00"), createdFee.getFee(), "Hall fee amount should match");
            
//             residentFee2024Id = createdFee.getId();
//         }
//     }

//     /**
//      * Test 3: Create second hall fee (attached 2024)
//      */
//     @Test
//     @Order(3)
//     void testCreateAttachedHallFee() {
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<HallFeeDTO> request = new HttpEntity<>(attachedFee2024, headers);

//         ResponseEntity<HallFeeDTO> response = restTemplate.exchange(
//             hallFeeBaseUrl, HttpMethod.POST, request, HallFeeDTO.class);

//         assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
//             "Creating attached hall fee should return CREATED status");
        
//         HallFeeDTO createdFee = response.getBody();
//         assertNotNull(createdFee, "Created hall fee should not be null");
//         if (createdFee != null) {
//             assertNotNull(createdFee.getId(), "Created hall fee should have an ID");
//             assertEquals("attached", createdFee.getType(), "Hall fee type should be attached");
//             assertEquals(Integer.valueOf(2024), createdFee.getYear(), "Hall fee year should be 2024");
//             assertEquals(new BigDecimal("12000.00"), createdFee.getFee(), "Hall fee amount should match");
            
//             attachedFee2024Id = createdFee.getId();
//         }
//     }

//     /**
//      * Test 4: Get all hall fees
//      */
//     @Test
//     @Order(4)
//     void testGetAllHallFees() {
//         // First create the fees
//         testCreateResidentHallFee();
//         testCreateAttachedHallFee();
        
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<Void> request = new HttpEntity<>(headers);

//         ResponseEntity<List<HallFeeDTO>> response = restTemplate.exchange(
//             hallFeeBaseUrl, HttpMethod.GET, request, 
//             new ParameterizedTypeReference<List<HallFeeDTO>>() {});

//         assertEquals(HttpStatus.OK, response.getStatusCode(), 
//             "Getting all hall fees should return OK status");
        
//         List<HallFeeDTO> fees = response.getBody();
//         assertNotNull(fees, "Hall fees list should not be null");
//         if (fees != null) {
//             assertTrue(fees.size() >= 2, "Should have at least 2 hall fees");
            
//             // Verify our test fees are in the list
//             boolean hasResident = fees.stream().anyMatch(fee -> 
//                 "resident".equals(fee.getType()) && fee.getYear() == 2024);
//             boolean hasAttached = fees.stream().anyMatch(fee -> 
//                 "attached".equals(fee.getType()) && fee.getYear() == 2024);
                
//             assertTrue(hasResident, "Should contain resident fee for 2024");
//             assertTrue(hasAttached, "Should contain attached fee for 2024");
//         }
//     }

//     /**
//      * Test 5: Get hall fee by ID
//      */
//     @Test
//     @Order(5)
//     void testGetHallFeeById() {
//         // First create fees
//         testCreateResidentHallFee();
//         testCreateAttachedHallFee();
        
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<Void> request = new HttpEntity<>(headers);

//         // Test getting resident fee by ID
//         ResponseEntity<HallFeeDTO> response = restTemplate.exchange(
//             hallFeeBaseUrl + "/" + residentFee2024Id, HttpMethod.GET, request, HallFeeDTO.class);

//         assertEquals(HttpStatus.OK, response.getStatusCode(), 
//             "Getting resident hall fee by ID should return OK status");
        
//         HallFeeDTO fee = response.getBody();
//         assertNotNull(fee, "Hall fee should not be null");
//         if (fee != null) {
//             assertEquals(residentFee2024Id, fee.getId(), "Fee ID should match");
//             assertEquals("resident", fee.getType(), "Fee type should be resident");
//             assertEquals(Integer.valueOf(2024), fee.getYear(), "Fee year should be 2024");
//         }

//         // Test getting attached fee by ID
//         ResponseEntity<HallFeeDTO> attachedResponse = restTemplate.exchange(
//             hallFeeBaseUrl + "/" + attachedFee2024Id, HttpMethod.GET, request, HallFeeDTO.class);

//         assertEquals(HttpStatus.OK, attachedResponse.getStatusCode(), 
//             "Getting attached hall fee by ID should return OK status");
        
//         HallFeeDTO attachedFee = attachedResponse.getBody();
//         assertNotNull(attachedFee, "Attached hall fee should not be null");
//         if (attachedFee != null) {
//             assertEquals(attachedFee2024Id, attachedFee.getId(), "Attached fee ID should match");
//             assertEquals("attached", attachedFee.getType(), "Fee type should be attached");
//             assertEquals(Integer.valueOf(2024), attachedFee.getYear(), "Fee year should be 2024");
//         }
//     }

//     /**
//      * Test 6: Get hall fee by non-existent ID
//      */
//     @Test
//     @Order(6)
//     void testGetHallFeeByNonExistentId() {
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<Void> request = new HttpEntity<>(headers);

//         ResponseEntity<HallFeeDTO> response = restTemplate.exchange(
//             hallFeeBaseUrl + "/99999", HttpMethod.GET, request, HallFeeDTO.class);

//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
//             "Getting non-existent hall fee should return NOT_FOUND status");
//     }

//     /**
//      * Test 7: Get hall fees by type
//      */
//     @Test
//     @Order(7)
//     void testGetHallFeesByType() {
//         // First create fees
//         testCreateResidentHallFee();
//         testCreateAttachedHallFee();
        
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<Void> request = new HttpEntity<>(headers);

//         ResponseEntity<List<HallFeeDTO>> response = restTemplate.exchange(
//             hallFeeBaseUrl + "/type/resident", HttpMethod.GET, request, 
//             new ParameterizedTypeReference<List<HallFeeDTO>>() {});

//         assertEquals(HttpStatus.OK, response.getStatusCode(), 
//             "Getting hall fees by type should return OK status");
        
//         List<HallFeeDTO> fees = response.getBody();
//         assertNotNull(fees, "Hall fees list should not be null");
//         if (fees != null) {
//             assertTrue(fees.size() >= 1, "Should have at least 1 resident fee");
            
//             // All fees should be resident type
//             boolean allResident = fees.stream().allMatch(fee -> "resident".equals(fee.getType()));
//             assertTrue(allResident, "All returned fees should be resident type");
//         }
//     }



//     /**
//      * Test 9: Get hall fees by year
//      */
//     @Test
//     @Order(9)
//     void testGetHallFeesByYear() {
//         // First create fees
//         testCreateResidentHallFee();
//         testCreateAttachedHallFee();
        
//         HttpHeaders headers = createAuthHeaders();
//         HttpEntity<Void> request = new HttpEntity<>(headers);

//         ResponseEntity<List<HallFeeDTO>> response = restTemplate.exchange(
//             hallFeeBaseUrl + "/year/2024", HttpMethod.GET, request, 
//             new ParameterizedTypeReference<List<HallFeeDTO>>() {});

//         assertEquals(HttpStatus.OK, response.getStatusCode(), 
//             "Getting hall fees by year should return OK status");
        
//         List<HallFeeDTO> fees = response.getBody();
//         assertNotNull(fees, "Hall fees list should not be null");
//         if (fees != null) {
//             assertTrue(fees.size() >= 2, "Should have at least 2 fees for 2024");
            
//             // All fees should be for year 2024
//             boolean allFor2024 = fees.stream().allMatch(fee -> fee.getYear() == 2024);
//             assertTrue(allFor2024, "All returned fees should be for year 2024");
//         }
//     }


//     /**
//      * Helper method to create authenticated headers
//      */
//     private HttpHeaders createAuthHeaders() {
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         headers.setBearerAuth(adminJwtToken);
//         return headers;
//     }
// }


