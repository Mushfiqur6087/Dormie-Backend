package com.HMS.hms.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.HMS.hms.DTO.SignupRequest;
import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.Students;
import com.HMS.hms.Tables.Users;
import com.HMS.hms.enums.UserRole;

@Service
public class UserService {
    
    @Autowired
    private UsersRepo usersRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private StudentsService studentsService;
    
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
    
    /**
     * Find users by role
     * @param role the role to search for
     * @return List<Users> containing users with the specified role
     */
    public List<Users> findByRole(String role) {
        return usersRepo.findByRole(role);
    }
    
    /**
     * Check if any admin user exists
     * @return true if at least one admin user exists, false otherwise
     */
    public boolean adminExists() {
        List<Users> adminUsers = usersRepo.findByRole(UserRole.ADMIN.getValue());
        return !adminUsers.isEmpty();
    }
    
    /**
     * Get count of admin users
     * @return number of admin users
     */
    public long getAdminCount() {
        List<Users> adminUsers = usersRepo.findByRole(UserRole.ADMIN.getValue());
        return adminUsers.size();
    }
    
    /**
     * Create a student user with associated student record
     * @param signUpRequest the signup request containing user and student data
     * @return the created Students object
     * @throws RuntimeException if studentId already exists or other validation fails
     */
    @Transactional
    public Students createStudentUser(SignupRequest signUpRequest) {
        // Validate studentId is provided
        if (signUpRequest.getStudentId() == null) {
            throw new RuntimeException("Student ID is required for student role!");
        }
        
        // Check if studentId already exists
        if (studentsService.existsByStudentId(signUpRequest.getStudentId())) {
            throw new RuntimeException("Student ID is already in use!");
        }
        
        // Check if email already exists
        if (existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user's account
        Users user = new Users();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(UserRole.STUDENT.getValue());

        // Save user first to get the userId
        Users savedUser = saveUser(user);
        
        // Create student entry
        Students student = new Students();
        student.setUser(savedUser); // Only set the user relationship, @MapsId will handle userId
        student.setStudentId(signUpRequest.getStudentId());
        
        // Parse username into first and last name
        parseAndSetStudentName(student, signUpRequest.getUsername());
        
        // Set default values
        setStudentDefaultValues(student);
        
        // Save student record
        return studentsService.saveStudent(student);
    }
    
    /**
     * Parse username and set first and last name for student
     * @param student the student object to update
     * @param username the username to parse
     */
    private void parseAndSetStudentName(Students student, String username) {
        String[] nameParts = username.trim().split("\\s+");
        switch (nameParts.length) {
            case 1 -> {
                student.setFirstName(nameParts[0]);
                student.setLastName("NONE");
            }
            case 2 -> {
                student.setFirstName(nameParts[0]);
                student.setLastName(nameParts[1]);
            }
            default -> {
                student.setFirstName(nameParts[0]);
                // Join from index 1 to end for last name
                StringBuilder lastName = new StringBuilder();
                for (int i = 1; i < nameParts.length; i++) {
                    if (i > 1) lastName.append(" ");
                    lastName.append(nameParts[i]);
                }
                student.setLastName(lastName.toString());
            }
        }
    }
    
    /**
     * Set default values for a new student
     * @param student the student object to update
     */
    private void setStudentDefaultValues(Students student) {
        student.setResidencyStatus("attached");
        student.setDepartment("Not Specified");
        student.setBatch(2025); // Current year as default batch
        student.setContactNo("Not Provided");
        student.setPresentAddress("Not Provided");
        student.setPermanentAddress("Not Provided");
    }
}
