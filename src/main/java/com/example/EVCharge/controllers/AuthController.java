package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.UserCredentials;
import com.example.EVCharge.dto.LoginResponse;
import com.example.EVCharge.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCredentials credentials) {
        try {
            return ResponseEntity.ok(authService.login(credentials));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
