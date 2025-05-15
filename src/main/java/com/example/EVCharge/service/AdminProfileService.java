package com.example.EVCharge.service;

import com.example.EVCharge.dto.ProfileUpdateRequest;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileService {

    @Autowired
    private UserRepository userRepository;

    // Отримання профілю адміністратора
    public User getAdminProfile(String username) {
        return userRepository.findByUsername(username);
    }

    // Оновлення профілю адміністратора
    public User updateAdminProfile(String currentUsername, ProfileUpdateRequest request) {
        User admin = userRepository.findByUsername(currentUsername);
        if (admin == null) return null;

        // Якщо логін змінюється – перевірити, що новий логін ще не зайнятий
        if (!request.getUsername().equals(currentUsername)) {
            User userWithNewUsername = userRepository.findByUsername(request.getUsername());
            if (userWithNewUsername != null) {
                throw new IllegalArgumentException("Цей логін вже зайнятий іншим користувачем");
            }
        }

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setUsername(request.getUsername());

        return userRepository.save(admin);
    }
}
