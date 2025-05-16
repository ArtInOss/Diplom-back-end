package com.example.EVCharge.service;

import com.example.EVCharge.dto.RegistrationRequest;

import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Реєстрація користувача
    public String registerUser(RegistrationRequest request) {
        // Перевірка: логін уже зайнятий
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return "Користувач з таким логіном вже існує!";
        }

        // Перевірка: паролі мають співпадати
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "Паролі не співпадають.";
        }

        // Створення користувача
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        newUser.setRole(Role.USER);

        userRepository.save(newUser);

        return "Реєстрація пройшла успішно!";
    }

    // Отримання профілю користувача
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> getAllUsersWithRoleUser() {
        return userRepository.findAllByRole(Role.USER);
    }
}

