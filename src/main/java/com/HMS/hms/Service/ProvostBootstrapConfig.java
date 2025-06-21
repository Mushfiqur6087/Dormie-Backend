package com.HMS.hms.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.HMS.hms.Repo.UsersRepo;
import com.HMS.hms.Tables.Users;
import com.HMS.hms.enums.UserRole;

/**
 * Bootstrap configuration to create a default admin user if no admin exists.
 * This ensures there's always an admin user available to create other users.
 */
@Component
public class ProvostBootstrapConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Default admin credentials - change these in production
    private static final String DEFAULT_PROVOST_USERNAME = "provost";
    private static final String DEFAULT_PROVOST_EMAIL = "provost@dormie.com";
    private static final String DEFAULT_PROVOST_PASSWORD = "Provost123!";

    @Override
    public void run(String... args) throws Exception {
        createDefaultProvostIfNotExists();
    }

    private void createDefaultProvostIfNotExists() {
        try {
            // Check if any admin user exists
            List<Users> provostUsers = usersRepo.findByRole(UserRole.PROVOST.getValue());

            if (provostUsers.isEmpty()) {
                logger.info("No PROVOST users found. Creating default PROVOST user...");

                // Check if the default admin email already exists with a different role
                if (usersRepo.findByEmail(DEFAULT_PROVOST_EMAIL).isPresent()) {
                    logger.warn("User with email {} already exists but is not an provost. Skipping provost creation.",
                            DEFAULT_PROVOST_EMAIL);
                    return;
                }

                // Check if the default admin username already exists
                if (usersRepo.findByUsername(DEFAULT_PROVOST_USERNAME).isPresent()) {
                    logger.warn("User with username {} already exists. Skipping provost creation.",
                            DEFAULT_PROVOST_USERNAME);
                    return;
                }

                // Create the default admin user
                Users adminUser = new Users();
                adminUser.setUsername(DEFAULT_PROVOST_USERNAME);
                adminUser.setEmail(DEFAULT_PROVOST_EMAIL);
                adminUser.setPassword(passwordEncoder.encode(DEFAULT_PROVOST_PASSWORD));
                adminUser.setRole(UserRole.PROVOST.getValue());
                adminUser.setCreatedAt(LocalDateTime.now());

                usersRepo.save(adminUser);

                logger.info("✅ Default provost user created successfully!");
                logger.info("📧 Email: {}", DEFAULT_PROVOST_EMAIL);
                logger.info("👤 Username: {}", DEFAULT_PROVOST_USERNAME);
                logger.info("🔑 Password: {}", DEFAULT_PROVOST_PASSWORD);
                logger.warn("⚠️  IMPORTANT: Please change the default admin password after first login!");

            } else {
                logger.info("Admin user(s) already exist. Count: {}", provostUsers.size());
            }

        } catch (Exception e) {
            logger.error("Error occurred while creating default admin user: {}", e.getMessage(), e);
        }
    }


}
