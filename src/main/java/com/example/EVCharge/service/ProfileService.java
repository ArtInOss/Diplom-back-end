package com.example.EVCharge.service;

import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    // Отримання профілю користувача
    public User getProfile(String username) {
        return userRepository.findByUsername(username);
    }

    // Оновлення профілю користувача
    public User updateProfile(String currentUsername, String firstName, String lastName, String newUsername) {
        User user = userRepository.findByUsername(currentUsername);
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(newUsername);
            return userRepository.save(user);
        }
        return null;
    }
}
