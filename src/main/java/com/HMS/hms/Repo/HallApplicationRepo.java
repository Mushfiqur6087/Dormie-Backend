package com.HMS.hms.Repo;

import com.HMS.hms.Tables.HallApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface HallApplicationRepo extends JpaRepository<HallApplication, Long> {
    Optional<HallApplication> findByUserId(Long userId); // Find a specific application by user ID
    Optional<HallApplication> findByStudentIdNo(Long studentIdNo); // Find by university student ID
    List<HallApplication> findByApplicationStatus(String applicationStatus); // Find by status (PENDING, APPROVED)
}