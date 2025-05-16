package com.example.EVCharge.service;

import com.example.EVCharge.dto.ProfileUpdateRequest;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public void updateAnyUserById(Long id, ProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        // Якщо логін змінюється — перевірити, що логін не зайнятий іншим
        if (!request.getUsername().equals(user.getUsername())) {
            User userWithNewUsername = userRepository.findByUsername(request.getUsername());
            if (userWithNewUsername != null && !userWithNewUsername.getId().equals(id)) {
                throw new IllegalArgumentException("Користувач із таким логіном уже існує");
            }
        }

        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        userRepository.save(user);
    }
}
