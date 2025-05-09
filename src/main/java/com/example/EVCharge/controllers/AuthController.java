package com.example.EVCharge.controllers;

import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // Обовʼязково

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody UserCredentials credentials) {
        User user = userRepository.findByUsername(credentials.getUsername());

        if (user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            String redirectUrl = user.getRole().equals(Role.ADMIN) ? "/admin.html" : "/user.html";
            return ResponseEntity.ok(new LoginResponse(true, redirectUrl));
        } else {
            return ResponseEntity.ok(new LoginResponse(false, ""));
        }
    }

    public static class UserCredentials {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private boolean success;
        private String redirectUrl;

        public LoginResponse(boolean success, String redirectUrl) {
            this.success = success;
            this.redirectUrl = redirectUrl;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }
    }
}
