package com.HMS.hms.Repo;

import com.HMS.hms.Tables.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUserId(Long userId);
}
