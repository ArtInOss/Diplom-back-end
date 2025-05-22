package com.example.EVCharge.config;

import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Створення ADMIN, якщо не існує
            if (userRepository.findByUsername("adminuser2") == null) {
                User admin = new User();
                admin.setUsername("adminuser2");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(Role.ADMIN);
                admin.setPassword(passwordEncoder.encode("admin321"));
                userRepository.save(admin);
                logger.info("✅ Адміністратора 'adminuser' створено з роллю ADMIN");
            } else {
                logger.info("ℹ️ Адміністратор 'adminuser' вже існує");
            }
        };
    }
}
