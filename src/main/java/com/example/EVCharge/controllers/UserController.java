package com.example.EVCharge.controllers;

import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Тестовий hello
    @GetMapping("/hello")
    public String helloUser() {
        return "Hello, USER!";
    }

    // Отримання поточного профілю
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }

        return ResponseEntity.ok(new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername()
        ));
    }

    // Оновлення профілю
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO updatedData, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }

        user.setFirstName(updatedData.getFirstName());
        user.setLastName(updatedData.getLastName());
        user.setUsername(updatedData.getUsername());

        userRepository.save(user);

        return ResponseEntity.ok("Дані профілю оновлено");
    }

    // DTO — щоб не передавати пароль!
    public static class UserDTO {
        private String firstName;
        private String lastName;
        private String username;

        public UserDTO() {}

        public UserDTO(String firstName, String lastName, String username) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
        }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
