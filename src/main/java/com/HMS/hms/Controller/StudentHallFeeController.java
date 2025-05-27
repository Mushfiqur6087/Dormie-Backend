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

import com.HMS.hms.DTO.StudentHallFeeDTO;
import com.HMS.hms.Service.StudentHallFeeService;

@RestController
@RequestMapping("/api/student-hall-fees")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentHallFeeController {

    @Autowired
    private StudentHallFeeService studentHallFeeService;

    // Get all student hall fees
    @GetMapping
    public ResponseEntity<List<StudentHallFeeDTO>> getAllStudentHallFees() {
        try {
            List<StudentHallFeeDTO> fees = studentHallFeeService.getAllStudentHallFeesDTO();
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student hall fee by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentHallFeeDTO> getStudentHallFeeById(@PathVariable Long id) {
        try {
            Optional<StudentHallFeeDTO> fee = studentHallFeeService.getStudentHallFeeByIdDTO(id);
            return fee.map(f -> new ResponseEntity<>(f, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student hall fees by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<StudentHallFeeDTO>> getStudentHallFeesByUserId(@PathVariable Long userId) {
        try {
            Optional<StudentHallFeeDTO> fees = studentHallFeeService.getStudentHallFeesByUserIdDTO(userId);
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get student hall fees by student ID
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentHallFeeDTO>> getStudentHallFeesByStudentId(@PathVariable Long studentId) {
        try {
            List<StudentHallFeeDTO> fees = studentHallFeeService.getStudentHallFeesByStudentIdDTO(studentId);
            return new ResponseEntity<>(fees, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
