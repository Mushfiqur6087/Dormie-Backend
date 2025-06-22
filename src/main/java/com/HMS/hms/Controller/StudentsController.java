package com.HMS.hms.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.StudentDTO;
import com.HMS.hms.DTO.StudentUpdateRequest;
import com.HMS.hms.Security.UserDetailsImpl;
import com.HMS.hms.Service.StudentsService;
import com.HMS.hms.Service.UserService;
import com.HMS.hms.Tables.Students;
import com.HMS.hms.Tables.Users;

import jakarta.validation.Valid;

//*Testing Done
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentsController {

    @Autowired
    private StudentsService studentsService;
    
    @Autowired
    private UserService userService;

    /**
     * Get all students info
     * @return List of all students 
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentsService.getAllStudentsAsDTO();
        return ResponseEntity.ok(students);
    }

    /**
     * Update student information
     * Extracts student email from JWT token and updates their information
     * @param updateRequest the student update request containing new information
     * @return ResponseEntity with updated student information or error message
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateStudentInformation(@Valid @RequestBody StudentUpdateRequest updateRequest) {
        try {
            // Get the email from the security context (JWT token)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = null;
            
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                email = userDetails.getEmail();
            }
            
            // Add debugging information
            System.out.println("DEBUG: Extracted email from JWT: " + email);
            System.out.println("DEBUG: Authentication object: " + authentication);
            System.out.println("DEBUG: Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
            
            if (email == null || email.equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Authentication required");
            }
            
            // Find the user by email
            Optional<Users> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                System.out.println("DEBUG: User not found with email: " + email);
                return ResponseEntity.status(404).body("User not found with email: " + email);
            }
            
            Users user = userOpt.get();
            System.out.println("DEBUG: Found user: " + user.getUsername() + " with role: " + user.getRole());
            
            // Check if user is a student
            if (!"STUDENT".equals(user.getRole())) {
                return ResponseEntity.status(403).body("Only students can update student information");
            }
            
            // Find the student record by user ID
            Optional<Students> studentOpt = studentsService.findByUserId(user.getUserId());
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Student record not found");
            }
            
            Students student = studentOpt.get();
            
            // Update the student information
            Students updatedStudent = studentsService.updateStudentInformation(student, updateRequest);
            
            // Convert to DTO and return
            StudentDTO studentDTO = studentsService.convertToDTO(updatedStudent);
            return ResponseEntity.ok(studentDTO);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating student information: " + e.getMessage());
        }
    }

    /**
     * Endpoint for an authenticated user to get their student ID by providing their email.
     * Only accessible by authenticated students, admins, or hall managers.
     * The email provided must match the authenticated user's email for students.
     * Admins/Hall Managers can retrieve any student's ID by email.
     *
     * @param email The email address of the student.
     * @return ResponseEntity with the student ID or an error message.
     */
    @GetMapping("/get-id-by-email") // Or /student-id?email={email}
    @PreAuthorize("isAuthenticated()") // Only authenticated users can access
    public ResponseEntity<?> getStudentIdByEmail(@RequestParam String email) {
        // Get the authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: Authentication required.");
        }

        // Attempt to retrieve student ID
        Optional<Long> studentIdOpt = studentsService.getStudentIdByEmail(email);

        if (studentIdOpt.isPresent()) {
            return ResponseEntity.ok(studentIdOpt.get()); // Return the student ID
        } else {
            // Return 404 if not found or if the email does not belong to a student
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student ID not found for provided email or user is not a student.");
        }
    }
}