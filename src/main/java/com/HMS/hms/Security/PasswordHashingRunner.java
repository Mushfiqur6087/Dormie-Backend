package com.HMS.hms.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.Users;

@Component
public class PasswordHashingRunner implements CommandLineRunner {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if we need to hash existing passwords
        List<Users> users = usersRepo.findAll();
        
        for (Users user : users) {
            // Check if password is already hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
            if (!user.getPassword().startsWith("$2")) {
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
                usersRepo.save(user);
                System.out.println("Hashed password for user: " + user.getEmail());
            }
        }
    }
}
