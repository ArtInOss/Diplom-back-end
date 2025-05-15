package com.example.EVCharge.service;

import com.example.EVCharge.dto.ProfileUpdateRequest;
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

    // Оновлення профілю користувача з перевіркою логіна
    public User updateProfile(String currentUsername, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(currentUsername);
        if (user == null) return null;

        String newUsername = request.getUsername();

        // Перевірка: чи логін вже зайнятий іншим
        if (!newUsername.equals(currentUsername)) {
            User userWithSameUsername = userRepository.findByUsername(newUsername);
            if (userWithSameUsername != null) {
                throw new IllegalArgumentException("Користувач з таким логіном вже існує");
            }
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(newUsername);

        return userRepository.save(user);
    }
}
