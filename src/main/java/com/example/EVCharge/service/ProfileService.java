package com.example.EVCharge.service;


import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    // Метод для отримання профілю користувача
    public User getProfile(String username) {
        // Шукаємо користувача в базі даних за username
        return userRepository.findByUsername(username);
    }
}
