package com.HMS.hms.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.hms.DTO.StudentDTO;
import com.HMS.hms.DTO.StudentUpdateRequest;
import com.HMS.hms.Repo.StudentsRepo;
import com.HMS.hms.Tables.Students;

@Service
public class StudentsService {
    
    @Autowired
    private StudentsRepo studentsRepo;
    
    /**
     * Get all students
     * @return List of all students
     */
    public List<Students> getAllStudents() {
        return studentsRepo.findAll();
    }
    
    /**
     * Find student by user ID
     * @param userId the user ID
     * @return Optional<Students> containing the student if found
     */
    public Optional<Students> findByUserId(Long userId) {
        return studentsRepo.findByUserId(userId);
    }
    
    /**
     * Find student by student ID
     * @param studentId the student ID
     * @return Optional<Students> containing the student if found
     */
    public Optional<Students> findByStudentId(Long studentId) {
        return studentsRepo.findByStudentId(studentId);
    }
    
    /**
     * Find student by registration number
     * @param regNo the registration number
     * @return Optional<Students> containing the student if found
     */
    public Optional<Students> findByRegNo(String regNo) {
        return studentsRepo.findByRegNo(regNo);
    }
    
    /**
     * Find students by department
     * @param department the department name
     * @return List of students in the department
     */
    public List<Students> findByDepartment(String department) {
        return studentsRepo.findByDepartment(department);
    }
    
    /**
     * Find students by batch
     * @param batch the batch year
     * @return List of students in the batch
     */
    public List<Students> findByBatch(Integer batch) {
        return studentsRepo.findByBatch(batch);
    }
    
    /**
     * Find students by residency status
     * @param residencyStatus the residency status (attached/resident)
     * @return List of students with the specified residency status
     */
    public List<Students> findByResidencyStatus(String residencyStatus) {
        return studentsRepo.findByResidencyStatus(residencyStatus);
    }
    
    /**
     * Find students by department and batch
     * @param department the department name
     * @param batch the batch year
     * @return List of students in the department and batch
     */
    public List<Students> findByDepartmentAndBatch(String department, Integer batch) {
        return studentsRepo.findByDepartmentAndBatch(department, batch);
    }
    
    /**
     * Save a student
     * @param student the student to save
     * @return the saved student
     */
    public Students saveStudent(Students student) {
        return studentsRepo.save(student);
    }
    
    /**
     * Check if student exists by student ID
     * @param studentId the student ID to check
     * @return true if student exists, false otherwise
     */
    public boolean existsByStudentId(Long studentId) {
        return studentsRepo.findByStudentId(studentId).isPresent();
    }
    
    /**
     * Check if student exists by registration number
     * @param regNo the registration number to check
     * @return true if student exists, false otherwise
     */
    public boolean existsByRegNo(String regNo) {
        return studentsRepo.findByRegNo(regNo).isPresent();
    }
    
    /**
     * Delete student by user ID
     * @param userId the user ID
     * @return true if student was deleted, false otherwise
     */
    public boolean deleteByUserId(Long userId) {
        if (studentsRepo.existsById(userId)) {
            studentsRepo.deleteById(userId);
            return true;
        }
        return false;
    }
    
    /**
     * Convert Students entity to StudentDTO
     * @param student the student entity
     * @return StudentDTO with student information only
     */
    public StudentDTO convertToDTO(Students student) {
        return new StudentDTO(
            student.getUserId(),
            student.getStudentId(),
            student.getFirstName(),
            student.getLastName(),
            student.getRegNo(),
            student.getDepartment(),
            student.getBatch(),
            student.getContactNo(),
            student.getPresentAddress(),
            student.getPermanentAddress(),
            student.getDateOfBirth(),
            student.getResidencyStatus()
        );
    }
    
    /**
     * Get all students as DTOs (without user sensitive information)
     * @return List of StudentDTOs
     */
    public List<StudentDTO> getAllStudentsAsDTO() {
        return studentsRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update student information
     * @param student the student entity to update
     * @param updateRequest the update request containing new information
     * @return the updated student entity
     */
    public Students updateStudentInformation(Students student, StudentUpdateRequest updateRequest) {
        // Update only the fields that are provided (not null)
        if (updateRequest.getDepartment() != null) {
            student.setDepartment(updateRequest.getDepartment());
        }
        
        if (updateRequest.getBatch() != null) {
            student.setBatch(updateRequest.getBatch());
        }
        
        if (updateRequest.getContactNo() != null) {
            student.setContactNo(updateRequest.getContactNo());
        }
        
        if (updateRequest.getPresentAddress() != null) {
            student.setPresentAddress(updateRequest.getPresentAddress());
        }
        
        if (updateRequest.getPermanentAddress() != null) {
            student.setPermanentAddress(updateRequest.getPermanentAddress());
        }
        
        if (updateRequest.getResidencyStatus() != null) {
            student.setResidencyStatus(updateRequest.getResidencyStatus());
        }
        
        return studentsRepo.save(student);
    }
}
