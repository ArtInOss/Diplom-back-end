package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.ProfileUpdateRequest;
import com.example.EVCharge.models.User;
import com.example.EVCharge.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ProfileService profileService;

    // 🔐 Доступ тільки для USER
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = profileService.getProfile(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Користувача не знайдено"));
        }

        return ResponseEntity.ok(Map.of(
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "username", user.getUsername()
        ));
    }

    // 🔐 Доступ тільки для USER
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateRequest request,
                                           BindingResult result,
                                           Authentication authentication) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", result.getAllErrors().get(0).getDefaultMessage()
            ));
        }

        String currentUsername = authentication.getName();

        try {
            User updatedUser = profileService.updateProfile(currentUsername, request);
            if (updatedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Користувача не знайдено"));
            }

            return ResponseEntity.ok(Map.of("message", "Дані профілю оновлено"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
