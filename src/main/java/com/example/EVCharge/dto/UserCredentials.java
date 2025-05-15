package com.example.EVCharge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCredentials {

    @NotBlank(message = "Логін не може бути порожнім")

    private String username;

    @NotBlank(message = "Пароль не може бути порожнім")

    private String password;

    // геттери і сеттери
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
