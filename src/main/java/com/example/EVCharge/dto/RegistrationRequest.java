package com.example.EVCharge.dto;

import jakarta.validation.constraints.*;

public class RegistrationRequest {



    @NotBlank(message = "Логін не може бути порожнім")
    @Size(max = 20, message = "Логін не може перевищувати 20 символів")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Логін може містити тільки латинські літери та цифри")
    private String username;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, max = 20, message = "Пароль має містити від 6 до 20 символів")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Пароль може містити тільки літери та цифри")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]+$",
            message = "Пароль має містити хоча б одну велику літеру, одну маленьку та одну цифру. Без спецсимволів."
    )
    private String password;

    @NotBlank(message = "Підтвердження паролю не може бути порожнім")
    private String confirmPassword;

    // Геттери та сеттери



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
