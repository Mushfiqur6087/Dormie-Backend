package com.HMS.hms.Repo;

import com.HMS.hms.Tables.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentsRepo extends JpaRepository<Students, Long> {
    // You can add custom queries later if needed
}
