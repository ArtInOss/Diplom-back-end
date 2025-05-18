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
    public void registerUser(RegistrationRequest request, Role role) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Користувач з таким логіном вже існує!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Паролі не співпадають.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);

        userRepository.save(newUser);
    }

    // Отримання профілю користувача
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> getAllUsersWithRoleUser() {
        return userRepository.findAllByRole(Role.USER);
    }
    public boolean deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}

