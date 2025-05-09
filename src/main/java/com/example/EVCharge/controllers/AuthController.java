package com.example.EVCharge.controllers;
import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/auth/login")  // ОБОВʼЯЗКОВО!
    public ResponseEntity<?> login(@RequestBody UserCredentials credentials) {
        User user = userRepository.findByUsername(credentials.getUsername());

        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            String redirectUrl = "/user.html";  // Если пользователь авторизован, редирект на user.html

            if (user.getRole().equals(Role.ADMIN)) {
                redirectUrl = "/admin.html";  // Если это админ, редирект на admin.html
            }

            return ResponseEntity.ok(new LoginResponse(true, redirectUrl));  // Успешный ответ с редиректом
        } else {
            return ResponseEntity.ok(new LoginResponse(false, ""));  // Ошибка авторизации
        }
    }

    // Вспомогательный класс для передачи данных из тела запроса
    public static class UserCredentials {
        private String username;
        private String password;

        // Геттеры и сеттеры
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

    // Вспомогательный класс для ответа от сервера
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
