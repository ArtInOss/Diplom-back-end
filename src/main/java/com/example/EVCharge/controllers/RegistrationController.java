package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.RegistrationRequest;
import com.example.EVCharge.models.Role;
import com.example.EVCharge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

        try {
            userService.registerUser(request, Role.USER);
            return ResponseEntity.ok(Map.of(
                    "message", "Реєстрація пройшла успішно!",
                    "redirectUrl", "/authorization.html"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
