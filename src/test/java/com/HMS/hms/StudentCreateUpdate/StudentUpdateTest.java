// package com.HMS.hms.StudentCreateUpdate;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import com.HMS.hms.DTO.StudentDTO;
// import com.HMS.hms.DTO.StudentUpdateRequest;
// import com.HMS.hms.utility.TestUtility;

// /**
//  * Integration test for student update functionality.
//  * 
//  * <p>Tests the complete workflow where admin creates students and then 
//  * students update their own information using the StudentsController.</p>
//  */
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
// @Transactional
// class StudentUpdateTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     private TestUtility testUtility;

//     /**
//      * Main test that verifies the complete student update workflow.
//      * 
//      * <p>Creates a student via admin, then logs in as that student and updates their information.</p>
//      */
//     @Test
//     void testStudentCanUpdateOwnInformation() {
//         // Initialize test utility
//         testUtility = new TestUtility(restTemplate, port);
        
//         // Step 1: Create a student via admin (this handles admin login and student creation)
//         TestUtility.StudentCredentials studentCredentials = testUtility.createStudentWithCredentials();
        
//         // Step 2: Login as the newly created student
//         String studentJwtToken = testUtility.loginAsStudent(
//             studentCredentials.getEmail(), 
//             studentCredentials.getPassword()
//         );
        
//         // Step 3: Student updates their own information
//         StudentUpdateRequest updateRequest = createStudentUpdateRequest();
//         updateStudentInformation(studentJwtToken, updateRequest);
//     }

//     /**
//      * Updates student information using the student's JWT token.
//      */
//     private void updateStudentInformation(String studentToken, StudentUpdateRequest updateRequest) {
//         HttpHeaders authHeaders = new HttpHeaders();
//         authHeaders.setContentType(MediaType.APPLICATION_JSON);
//         authHeaders.setBearerAuth(studentToken);
//         HttpEntity<StudentUpdateRequest> request = new HttpEntity<>(updateRequest, authHeaders);

//         ResponseEntity<StudentDTO> response = restTemplate.exchange(
//             testUtility.getStudentsBaseUrl() + "/update", HttpMethod.PUT, request, StudentDTO.class);

//         assertEquals(HttpStatus.OK, response.getStatusCode(), "Student information update should be successful");
        
//         StudentDTO updatedStudent = response.getBody();
//         assertNotNull(updatedStudent, "Updated student information should not be null");
        
//         // Verify the update was successful by checking key fields
//         // Use explicit null checks to avoid static analysis warnings
//         if (updatedStudent != null) {
//             assertEquals(updateRequest.getDepartment(), updatedStudent.getDepartment(), 
//                 "Department should be updated");
//             assertEquals(updateRequest.getBatch(), updatedStudent.getBatch(), 
//                 "Batch should be updated");
//             assertEquals(updateRequest.getContactNo(), updatedStudent.getContactNo(), 
//                 "Contact number should be updated");
//             assertEquals(updateRequest.getPresentAddress(), updatedStudent.getPresentAddress(), 
//                 "Present address should be updated");
//             assertEquals(updateRequest.getPermanentAddress(), updatedStudent.getPermanentAddress(), 
//                 "Permanent address should be updated");
//             assertEquals(updateRequest.getResidencyStatus(), updatedStudent.getResidencyStatus(), 
//                 "Residency status should be updated");
//         }
//     }

//     /**
//      * Creates a StudentUpdateRequest with test data.
//      */
//     private StudentUpdateRequest createStudentUpdateRequest() {
//         StudentUpdateRequest updateRequest = new StudentUpdateRequest();
//         updateRequest.setDepartment("Computer Science");
//         updateRequest.setBatch(2024);
//         updateRequest.setContactNo("01712345678");
//         updateRequest.setPresentAddress("123 University Campus, Dhaka");
//         updateRequest.setPermanentAddress("456 Home Village, Chittagong");
//         updateRequest.setResidencyStatus("resident");
//         return updateRequest;
//     }
// }
