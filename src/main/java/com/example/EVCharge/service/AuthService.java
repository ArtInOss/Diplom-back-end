package com.example.EVCharge.service;

import com.example.EVCharge.dto.UserCredentials;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import com.example.EVCharge.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.EVCharge.dto.LoginResponse;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(UserCredentials credentials) {
        User user = userRepository.findByUsername(credentials.getUsername());

        if (user == null || !passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            throw new RuntimeException("Невірний логін або пароль");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
        String redirectUrl = switch (user.getRole()) {
            case ADMIN -> "/admin.html";
            case USER -> "/user.html";
            default -> "/";
        };

        return new LoginResponse(true, redirectUrl, token);
    }
}
