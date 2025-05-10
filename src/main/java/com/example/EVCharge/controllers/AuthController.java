package com.example.EVCharge.controllers;

import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import com.example.EVCharge.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")  // Обновляем путь
    public ResponseEntity<?> login(@RequestBody UserCredentials credentials) {
        User user = userRepository.findByUsername(credentials.getUsername());

        if (user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            // Определяем URL редиректа в зависимости от роли пользователя
            String redirectUrl = user.getRole().equals(Role.ADMIN) ? "/admin.html" : "/user.html";

            // Генерируем JWT токен
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());

            // Возвращаем успешный ответ с токеном и URL для редиректа
            return ResponseEntity.ok(new TokenResponse(true, redirectUrl, token));
        } else {
            // Если авторизация не успешна, возвращаем ошибку
            return ResponseEntity.ok(new TokenResponse(false, "", ""));
        }
    }

    // DTO для получения данных из запроса
    public static class UserCredentials {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // DTO для отправки ответа
    public static class TokenResponse {
        private boolean success;
        private String redirectUrl;  // Используем правильное имя
        private String token;

        public TokenResponse(boolean success, String redirectUrl, String token) {
            this.success = success;
            this.redirectUrl = redirectUrl;
            this.token = token;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getRedirectUrl() { return redirectUrl; }  // Получаем redirectUrl
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
