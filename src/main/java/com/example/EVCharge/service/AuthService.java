package com.example.EVCharge.service;

import com.example.EVCharge.dto.UserCredentials;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import com.example.EVCharge.security.JwtUtil;
import com.example.EVCharge.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(UserCredentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Невірний логін або пароль");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
        String redirectUrl = switch (user.getRole()) {
            case ADMIN -> "/admin.html";
            case USER -> "/user.html";
            default -> "/";
        };

        return new LoginResponse(true, redirectUrl, token);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public List<String> extractRoles(String token) {
        String role = jwtUtil.getRoleFromToken(token);
        return role != null ? List.of(role) : Collections.emptyList();
    }
}
