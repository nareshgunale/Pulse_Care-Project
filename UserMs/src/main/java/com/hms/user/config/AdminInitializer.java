package com.hms.user.config;

import com.hms.user.constant.Roles;
import com.hms.user.entity.User;
import com.hms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByRole(Roles.ADMIN)) {
            System.out.println("ADMIN user already exists.");
            return;
        }

        User admin = new User();

        admin.setName("System Admin");
        admin.setEmail("admin@pulsecare.com");

        // Change this temporary password before running if you want.
        admin.setPassword(passwordEncoder.encode("Admin@123"));

        admin.setRole(Roles.ADMIN);
        admin.setProfileId(null);

        userRepository.save(admin);

        System.out.println("======================================");
        System.out.println("INITIAL ADMIN CREATED");
        System.out.println("Email: admin@pulsecare.com");
        System.out.println("======================================");
    }
}