package com.HMS.hms.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.Users;

@Service
public class UserService {
    
    @Autowired
    private UsersRepo usersRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate user by email and password
     * @param email the email
     * @param password the password
     * @return Optional<Users> containing the user if authentication is successful
     */
    public Optional<Users> authenticateUserByEmail(String email, String password) {
        Optional<Users> userOpt = usersRepo.findByEmail(email);
        
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // Use BCrypt password encoder for production security
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    
    /**
     * Find user by username
     * @param username the username
     * @return Optional<Users> containing the user if found
     */
    public Optional<Users> findByUsername(String username) {
        return usersRepo.findByUsername(username);
    }
    
    /**
     * Find user by email
     * @param email the email
     * @return Optional<Users> containing the user if found
     */
    public Optional<Users> findByEmail(String email) {
        return usersRepo.findByEmail(email);
    }
    
    /**
     * Find user by user ID
     * @param userId the user ID
     * @return Optional<Users> containing the user if found
     */
    public Optional<Users> findByUserId(Long userId) {
        return usersRepo.findByUserId(userId);
    }
    
    /**
     * Save a user
     * @param user the user to save
     * @return the saved user
     */
    public Users saveUser(Users user) {
        return usersRepo.save(user);
    }
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return usersRepo.findByUsername(username).isPresent();
    }
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return usersRepo.findByEmail(email).isPresent();
    }
}
