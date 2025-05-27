package com.HMS.hms.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.hms.DTO.StudentDTO;
import com.HMS.hms.Service.StudentsService;
//*Testing Done
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentsController {

    @Autowired
    private StudentsService studentsService;

    /**
     * Get all students info
     * @return List of all students (without sensitive user information)
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentsService.getAllStudentsAsDTO();
        return ResponseEntity.ok(students);
    }
}
