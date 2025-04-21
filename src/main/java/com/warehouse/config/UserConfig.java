package com.warehouse.config;

import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class UserConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            try {
                // Create admin user if not exists
                if (userRepository.findByUsername("admin").isEmpty()) {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("admin"));
                    admin.setEmail("admin@warehouse.com");
                    admin.setFullName("System Administrator");
                    admin.setEnabled(true);
                    admin.getRoles().clear(); // Clear default USER role
                    admin.addRole(Role.ADMIN);
                    userRepository.save(admin);
                    logger.info("Admin user created successfully");
                }

                // Create regular user if not exists
                if (userRepository.findByUsername("user").isEmpty()) {
                    User user = new User();
                    user.setUsername("user");
                    user.setPassword(passwordEncoder.encode("user"));
                    user.setEmail("user@warehouse.com");
                    user.setFullName("Regular User");
                    user.setEnabled(true);
                    // No need to add USER role as it's added by default in User constructor
                    userRepository.save(user);
                    logger.info("Regular user created successfully");
                }
            } catch (Exception e) {
                logger.error("Error initializing users: " + e.getMessage(), e);
                throw e;
            }
        };
    }
} 