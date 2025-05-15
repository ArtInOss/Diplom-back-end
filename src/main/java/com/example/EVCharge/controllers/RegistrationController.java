package com.example.EVCharge.controllers;

import com.example.EVCharge.dto.RegistrationRequest;
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
        // 🔍 Перевірка валідації DTO
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

        // 👉 Перевірка з сервісу (чи користувач існує, чи паролі однакові)
        String message = userService.registerUser(request);

        if (!message.equals("Реєстрація пройшла успішно!")) {
            return ResponseEntity.badRequest().body(Map.of("message", message));
        }

        return ResponseEntity.ok(Map.of(
                "message", message,
                "redirectUrl", "/authorization.html"
        ));
    }
}
