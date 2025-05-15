package com.example.EVCharge.service;

import com.example.EVCharge.dto.RegistrationRequest;
import com.example.EVCharge.models.Role;
import com.example.EVCharge.models.User;
import com.example.EVCharge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Реєстрація користувача
    public String registerUser(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return "Користувач з таким логіном вже існує!";
        }
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return "Логін не може бути порожнім.";
        }
        if (!request.getUsername().matches("^[a-zA-Z0-9]+$")) {
            return "Логін може містити тільки літери та цифри без пробілів і спеціальних знаків.";
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return "Пароль не може бути порожнім.";
        }
        if (!isStrongPassword(request.getPassword())) {
            return "Пароль має містити хоча б одну цифру, велику і маленьку літеру.";
        }
        if (!request.getPassword().matches("^[a-zA-Z0-9]+$")) {
            return "Пароль може містити тільки літери, цифри без пробілів і спеціальних знаків.";
        }


        // Добавьте проверку на confirmPassword
        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            return "Підтвердження паролю не може бути порожнім.";
        }
        if (request.getUsername().length() > 20) {
            return "Логін не може перевищувати 20 символів.";
        }

        if (request.getPassword().length() > 20) {
            return "Пароль не може перевищувати 20 символів.";
        }

        if (!request.getPassword().trim().equals(request.getConfirmPassword().trim())) {
            return "Паролі не співпадають.";
        }

        if (request.getPassword().length() < 6) {
            return "Пароль має містити щонайменше 6 символів.";
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setRole(Role.USER); // или другая логика для роли

        userRepository.save(newUser);

        return "Реєстрація пройшла успішно!";
    }

    // Отримання профілю користувача
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }

    // Оновлення профілю користувача

    private boolean isStrongPassword(String password) {
        // Перевірка на наявність хоча б однієї великої літери, маленької літери, цифри
        return password.matches(".*[A-Z].*") &&   // Велика буква
                password.matches(".*[a-z].*") &&   // Мала буква
                password.matches(".*\\d.*");       // Цифра
    }
}
