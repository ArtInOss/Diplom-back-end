package com.example.EVCharge.config;

import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {


            // Створення ADMIN, якщо не існує
            if (userRepository.findByUsername("adminuser") == null) {
                User admin = new User();
                admin.setUsername("adminuser");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                admin.setRole(Role.ADMIN);
                admin.setPassword(passwordEncoder.encode("admin321"));
                userRepository.save(admin);
                System.out.println("✅ Адміністратора 'adminuser' створено з роллю ADMIN");
            } else {
                System.out.println("ℹ️ Адміністратор 'adminuser' вже існує");
            }
        };
    }
}
