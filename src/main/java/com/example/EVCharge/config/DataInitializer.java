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
            if (userRepository.findByUsername("secureuser") == null) {
                User user = new User();
                user.setUsername("secureuser");
                user.setFirstName("Secure");
                user.setLastName("User");
                user.setRole(Role.USER);

                // Хешування паролю
                user.setPassword(passwordEncoder.encode("pass321"));

                userRepository.save(user);
                System.out.println("✅ Користувача 'secureuser' створено з хешованим паролем");
            } else {
                System.out.println("ℹ️ Користувач 'secureuser' вже існує");
            }
        };
    }
}
