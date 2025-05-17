package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.ProfileUpdateRequest;
import com.example.EVCharge.models.User;
import com.example.EVCharge.service.AdminProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.EVCharge.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")

public class AdminController {

    @Autowired
    private AdminProfileService adminProfileService;
    @Autowired
    private UserService userService;

    // Отримати профіль адміністратора
    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(Authentication authentication) {
        User user = adminProfileService.getAdminProfile(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Адміністратора не знайдено"));
        }

        return ResponseEntity.ok(new UserDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername()
        ));
    }

    // Оновити профіль адміністратора (як у UserController)
    @PutMapping("/profile")
    public ResponseEntity<?> updateAdminProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            BindingResult result,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            User updatedUser = adminProfileService.updateAdminProfile(authentication.getName(), request);
            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Адміністратора не знайдено"));
            }

            return ResponseEntity.ok(Map.of("message", "Дані адміністратора оновлено"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DTO для відповіді
    public static class UserDTO {
        private String firstName;
        private String lastName;
        private String username;

        public UserDTO(String firstName, String lastName, String username) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUsername() {
            return username;
        }
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersWithRoleUser() {
        List<User> users = userService.getAllUsersWithRoleUser();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUserByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody ProfileUpdateRequest request,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            adminProfileService.updateAnyUserById(id, request);  // ДЕЛЕГУЄМО СЮДИ
            return ResponseEntity.ok(Map.of("message", "Користувача оновлено"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);

        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Користувача успішно видалено"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Користувача не знайдено"));
        }
    }
}
