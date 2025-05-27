package com.HMS.hms.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HMS.hms.Tables.Students;

@Repository
public interface StudentsRepo extends JpaRepository<Students, Long> {
    
    // Find by studentId
    Optional<Students> findByStudentId(Long studentId);
    
    // Find by userId
    Optional<Students> findByUserId(Long userId);
    
    // Find by registration number
    Optional<Students> findByRegNo(String regNo);
    
    // Find by department
    List<Students> findByDepartment(String department);
    
    // Find by batch
    List<Students> findByBatch(Integer batch);
    
    // Find by residency status
    List<Students> findByResidencyStatus(String residencyStatus);
    
    // Find by department and batch
    List<Students> findByDepartmentAndBatch(String department, Integer batch);
}
