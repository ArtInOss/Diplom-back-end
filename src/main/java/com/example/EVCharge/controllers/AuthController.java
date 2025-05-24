package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.UserCredentials;
import com.example.EVCharge.dto.LoginResponse;
import com.example.EVCharge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    //  Авторизація користувача
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserCredentials credentials, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

        try {
            LoginResponse response = authService.login(credentials);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }

    //  Перевірка JWT токена
    @GetMapping("/check")
    public ResponseEntity<?> checkToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Відсутній токен"));
        }

        String token = authHeader.substring(7); // прибираємо "Bearer "

        if (!authService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("message", "Недійсний токен"));
        }

        String username = authService.extractUsername(token);
        List<String> roles = authService.extractRoles(token);

        return ResponseEntity.ok(Map.of(
                "username", username,
                "roles", roles
        ));
    }
}
