package com.HMS.hms.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.StudentDTO;
import com.HMS.hms.DTO.StudentUpdateRequest;
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
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            
            if (email == null || email.equals("anonymousUser")) {
                return ResponseEntity.status(401).body("Authentication required");
            }
            
            // Find the user by email
            Optional<Users> userOpt = userService.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }
            
            Users user = userOpt.get();
            
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
}