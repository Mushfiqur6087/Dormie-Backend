package com.HMS.hms.DiningFeeTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.HMS.hms.DTO.DiningFeeDTO;
import com.HMS.hms.utility.TestUtility;

/**
 * Comprehensive integration tests for DiningFeeController.
 * 
 * <p>Tests all CRUD operations and business logic for dining fee management.
 * Since dining fees are only for resident students, all tests focus on resident type.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DiningFeeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private TestUtility testUtility;
    private String adminJwtToken;
    private String diningFeeBaseUrl;
    
    // Test data
    private DiningFeeDTO residentFee2024;
    private DiningFeeDTO residentFee2025;
    private Long residentFee2024Id;
    private Long residentFee2025Id;

    @BeforeEach
    public void setUp() {
        testUtility = new TestUtility(restTemplate, port);
        adminJwtToken = testUtility.loginAsAdmin();
        diningFeeBaseUrl = "http://localhost:" + port + "/api/dining-fees";
        
        // Initialize test data - dining fees are only for residents
        residentFee2024 = new DiningFeeDTO(
            "resident", 
            2024, 
            LocalDate.of(2024, 1, 1), 
            LocalDate.of(2024, 12, 31), 
            new BigDecimal("8000.00")
        );
        
        residentFee2025 = new DiningFeeDTO(
            "resident", 
            2025, 
            LocalDate.of(2025, 1, 1), 
            LocalDate.of(2025, 12, 31), 
            new BigDecimal("8500.00")
        );
    }

    /**
     * Test 1: Admin authentication
     */
    @Test
    @Order(1)
    void testAdminCanAuthenticate() {
        assertNotNull(adminJwtToken, "Admin should be able to login and get JWT token");
        assertTrue(adminJwtToken.length() > 0, "JWT token should not be empty");
    }

    /**
     * Test 2: Create first dining fee (resident 2024)
     */
    @Test
    @Order(2)
    void testCreateResidentDiningFee2024() {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<DiningFeeDTO> request = new HttpEntity<>(residentFee2024, headers);

        ResponseEntity<DiningFeeDTO> response = restTemplate.exchange(
            diningFeeBaseUrl, HttpMethod.POST, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
            "Creating resident dining fee should return CREATED status");
        
        DiningFeeDTO createdFee = response.getBody();
        assertNotNull(createdFee, "Created dining fee should not be null");
        if (createdFee != null) {
            assertNotNull(createdFee.getId(), "Created dining fee should have an ID");
            assertEquals("resident", createdFee.getType(), "Dining fee type should be resident");
            assertEquals(Integer.valueOf(2024), createdFee.getYear(), "Dining fee year should be 2024");
            assertEquals(new BigDecimal("8000.00"), createdFee.getFee(), "Dining fee amount should match");
            assertEquals(LocalDate.of(2024, 1, 1), createdFee.getStartDate(), "Start date should match");
            assertEquals(LocalDate.of(2024, 12, 31), createdFee.getEndDate(), "End date should match");
            
            residentFee2024Id = createdFee.getId();
        }
    }

    /**
     * Test 3: Create second dining fee (resident 2025)
     */
    @Test
    @Order(3)
    void testCreateResidentDiningFee2025() {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<DiningFeeDTO> request = new HttpEntity<>(residentFee2025, headers);

        ResponseEntity<DiningFeeDTO> response = restTemplate.exchange(
            diningFeeBaseUrl, HttpMethod.POST, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), 
            "Creating resident dining fee should return CREATED status");
        
        DiningFeeDTO createdFee = response.getBody();
        assertNotNull(createdFee, "Created dining fee should not be null");
        if (createdFee != null) {
            assertNotNull(createdFee.getId(), "Created dining fee should have an ID");
            assertEquals("resident", createdFee.getType(), "Dining fee type should be resident");
            assertEquals(Integer.valueOf(2025), createdFee.getYear(), "Dining fee year should be 2025");
            assertEquals(new BigDecimal("8500.00"), createdFee.getFee(), "Dining fee amount should match");
            assertEquals(LocalDate.of(2025, 1, 1), createdFee.getStartDate(), "Start date should match");
            assertEquals(LocalDate.of(2025, 12, 31), createdFee.getEndDate(), "End date should match");
            
            residentFee2025Id = createdFee.getId();
        }
    }

    /**
     * Test 4: Get all dining fees
     */
    @Test
    @Order(4)
    void testGetAllDiningFees() {
        // First create the fees
        testCreateResidentDiningFee2024();
        testCreateResidentDiningFee2025();
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<DiningFeeDTO>> response = restTemplate.exchange(
            diningFeeBaseUrl, HttpMethod.GET, request, 
            new ParameterizedTypeReference<List<DiningFeeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Getting all dining fees should return OK status");
        
        List<DiningFeeDTO> fees = response.getBody();
        assertNotNull(fees, "Dining fees list should not be null");
        if (fees != null) {
            assertTrue(fees.size() >= 2, "Should have at least 2 dining fees");
            
            // Verify our test fees are in the list
            boolean has2024 = fees.stream().anyMatch(fee -> 
                "resident".equals(fee.getType()) && fee.getYear() == 2024);
            boolean has2025 = fees.stream().anyMatch(fee -> 
                "resident".equals(fee.getType()) && fee.getYear() == 2025);
                
            assertTrue(has2024, "Should contain resident fee for 2024");
            assertTrue(has2025, "Should contain resident fee for 2025");
            
            // All fees should be resident type since dining fees are only for residents
            boolean allResident = fees.stream().allMatch(fee -> "resident".equals(fee.getType()));
            assertTrue(allResident, "All dining fees should be resident type");
        }
    }

    /**
     * Test 5: Get dining fee by ID
     */
    @Test
    @Order(5)
    void testGetDiningFeeById() {
        // First create fees
        testCreateResidentDiningFee2024();
        testCreateResidentDiningFee2025();
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Test getting 2024 fee by ID
        ResponseEntity<DiningFeeDTO> response = restTemplate.exchange(
            diningFeeBaseUrl + "/" + residentFee2024Id, HttpMethod.GET, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Getting resident dining fee by ID should return OK status");
        
        DiningFeeDTO fee = response.getBody();
        assertNotNull(fee, "Dining fee should not be null");
        if (fee != null) {
            assertEquals(residentFee2024Id, fee.getId(), "Fee ID should match");
            assertEquals("resident", fee.getType(), "Fee type should be resident");
            assertEquals(Integer.valueOf(2024), fee.getYear(), "Fee year should be 2024");
            assertEquals(new BigDecimal("8000.00"), fee.getFee(), "Fee amount should match");
        }

        // Test getting 2025 fee by ID
        ResponseEntity<DiningFeeDTO> response2025 = restTemplate.exchange(
            diningFeeBaseUrl + "/" + residentFee2025Id, HttpMethod.GET, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.OK, response2025.getStatusCode(), 
            "Getting resident dining fee by ID should return OK status");
        
        DiningFeeDTO fee2025 = response2025.getBody();
        assertNotNull(fee2025, "Dining fee should not be null");
        if (fee2025 != null) {
            assertEquals(residentFee2025Id, fee2025.getId(), "Fee ID should match");
            assertEquals("resident", fee2025.getType(), "Fee type should be resident");
            assertEquals(Integer.valueOf(2025), fee2025.getYear(), "Fee year should be 2025");
            assertEquals(new BigDecimal("8500.00"), fee2025.getFee(), "Fee amount should match");
        }
    }

    /**
     * Test 6: Get dining fee by non-existent ID
     */
    @Test
    @Order(6)
    void testGetDiningFeeByNonExistentId() {
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<DiningFeeDTO> response = restTemplate.exchange(
            diningFeeBaseUrl + "/99999", HttpMethod.GET, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
            "Getting non-existent dining fee should return NOT_FOUND status");
    }

    /**
     * Test 7: Get dining fees by year
     */
    @Test
    @Order(7)
    void testGetDiningFeesByYear() {
        // First create fees
        testCreateResidentDiningFee2024();
        testCreateResidentDiningFee2025();
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<DiningFeeDTO>> response = restTemplate.exchange(
            diningFeeBaseUrl + "/year/2024", HttpMethod.GET, request, 
            new ParameterizedTypeReference<List<DiningFeeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Getting dining fees by year should return OK status");
        
        List<DiningFeeDTO> fees = response.getBody();
        assertNotNull(fees, "Dining fees list should not be null");
        if (fees != null) {
            assertTrue(fees.size() >= 1, "Should have at least 1 fee for 2024");
            
            // All fees should be for year 2024
            boolean allFor2024 = fees.stream().allMatch(fee -> fee.getYear() == 2024);
            assertTrue(allFor2024, "All returned fees should be for year 2024");
            
            // All fees should be resident type
            boolean allResident = fees.stream().allMatch(fee -> "resident".equals(fee.getType()));
            assertTrue(allResident, "All dining fees should be resident type");
        }
    }


    /**
     * Test 8: Get active dining fees
     */
    @Test
    @Order(8)
    void testGetActiveDiningFees() {
        // Create a fee that should be active (current year)
        DiningFeeDTO activeFee = new DiningFeeDTO(
            "resident", 
            2025, 
            LocalDate.of(2025, 1, 1), 
            LocalDate.of(2025, 12, 31), 
            new BigDecimal("9000.00")
        );
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<DiningFeeDTO> createRequest = new HttpEntity<>(activeFee, headers);
        restTemplate.exchange(diningFeeBaseUrl, HttpMethod.POST, createRequest, DiningFeeDTO.class);
        
        HttpEntity<Void> getRequest = new HttpEntity<>(headers);
        ResponseEntity<List<DiningFeeDTO>> response = restTemplate.exchange(
            diningFeeBaseUrl + "/active", HttpMethod.GET, getRequest, 
            new ParameterizedTypeReference<List<DiningFeeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Getting active dining fees should return OK status");
        
        List<DiningFeeDTO> fees = response.getBody();
        assertNotNull(fees, "Active dining fees list should not be null");
        if (fees != null) {
            // All fees should be resident type
            boolean allResident = fees.stream().allMatch(fee -> "resident".equals(fee.getType()));
            assertTrue(allResident, "All active dining fees should be resident type");
        }
    }

    /**
     * Test 9: Get dining fees in date range
     */
    @Test
    @Order(9)
    void testGetDiningFeesInDateRange() {
        // First create fees
        testCreateResidentDiningFee2024();
        testCreateResidentDiningFee2025();
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String startDate = "2024-01-01";
        String endDate = "2024-12-31";
        
        ResponseEntity<List<DiningFeeDTO>> response = restTemplate.exchange(
            diningFeeBaseUrl + "/date-range?startDate=" + startDate + "&endDate=" + endDate, 
            HttpMethod.GET, request, 
            new ParameterizedTypeReference<List<DiningFeeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Getting dining fees in date range should return OK status");
        
        List<DiningFeeDTO> fees = response.getBody();
        assertNotNull(fees, "Dining fees list should not be null");
        if (fees != null) {
            // All fees should be resident type
            boolean allResident = fees.stream().allMatch(fee -> "resident".equals(fee.getType()));
            assertTrue(allResident, "All dining fees should be resident type");
            
            // Fees should be within the date range
            boolean withinRange = fees.stream().allMatch(fee -> 
                !fee.getStartDate().isAfter(LocalDate.parse(endDate)) &&
                !fee.getEndDate().isBefore(LocalDate.parse(startDate)));
            assertTrue(withinRange, "All fees should be within the specified date range");
        }
    }

    
    /**
     * Test 10: Test validation with invalid fee amount
     */
    @Test
    @Order(10)
    void testCreateDiningFeeWithInvalidAmount() {
        DiningFeeDTO invalidFee = new DiningFeeDTO(
            "resident", 
            2024, 
            LocalDate.of(2024, 1, 1), 
            LocalDate.of(2024, 12, 31), 
            new BigDecimal("0.00") // Invalid amount (should be > 0)
        );
        
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<DiningFeeDTO> request = new HttpEntity<>(invalidFee, headers);

        ResponseEntity<DiningFeeDTO> response = restTemplate.exchange(
            diningFeeBaseUrl, HttpMethod.POST, request, DiningFeeDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), 
            "Creating dining fee with invalid amount should return UNAUTHORIZED status");
    }

    /**
     * Helper method to create authenticated headers
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminJwtToken);
        return headers;
    }
}
