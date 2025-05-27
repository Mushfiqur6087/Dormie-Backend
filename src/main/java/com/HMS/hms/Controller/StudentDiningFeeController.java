package com.HMS.hms.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.StudentDiningFeeDTO;
import com.HMS.hms.Service.StudentDiningFeeService;

@RestController
@RequestMapping("/api/student-dining-fees")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentDiningFeeController {

    @Autowired
    private StudentDiningFeeService studentDiningFeeService;

    // Get all student dining fees
    @GetMapping
    public ResponseEntity<List<StudentDiningFeeDTO>> getAllStudentDiningFees() {
        try {
            List<StudentDiningFeeDTO> fees = studentDiningFeeService.getAllStudentDiningFeesDTO();
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student dining fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentDiningFeeDTO> getStudentDiningFeeById(@PathVariable Long id) {
        try {
            Optional<StudentDiningFeeDTO> fee = studentDiningFeeService.getStudentDiningFeeByIdDTO(id);
            return fee.map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student dining fees by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StudentDiningFeeDTO>> getStudentDiningFeesByUserId(@PathVariable Long userId) {
        try {
            List<StudentDiningFeeDTO> fees = studentDiningFeeService.getStudentDiningFeesByUserIdDTO(userId);
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student dining fees by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentDiningFeeDTO>> getStudentDiningFeesByStudentId(@PathVariable Long studentId) {
        try {
            List<StudentDiningFeeDTO> fees = studentDiningFeeService.getStudentDiningFeesByStudentIdDTO(studentId);
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
