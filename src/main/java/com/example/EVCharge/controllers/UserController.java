package com.example.EVCharge.controllers;

import com.example.EVCharge.models.User;
import com.example.EVCharge.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ProfileService profileService;

    // Отримання поточного профілю
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = profileService.getProfile(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }

        return ResponseEntity.ok(new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername()
        ));
    }

    // Оновлення профілю користувача
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO updatedData, Authentication authentication) {
        String username = authentication.getName();
        User updatedUser = profileService.updateProfile(username,
                updatedData.getFirstName(),
                updatedData.getLastName(),
                updatedData.getUsername());

        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Користувача не знайдено");
        }

        return ResponseEntity.ok("Дані профілю оновлено");
    }

    // DTO для передачі профілю користувача
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
