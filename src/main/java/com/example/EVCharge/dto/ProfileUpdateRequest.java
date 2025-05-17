package com.example.EVCharge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {


    @Size(max = 30, message = "Ім’я не може бути довшим за 30 символів")
    @Pattern(regexp = "^[А-Яа-яЇїІіЄєA-Za-z\\-ʼ’]*$", message = "Ім’я може містити тільки літери")
    private String firstName;

    @Size(max = 30, message = "Прізвище не може бути довшим за 30 символів")
    @Pattern(regexp = "^[А-Яа-яЇїІіЄєA-Za-z\\-ʼ’]*$", message = "Прізвище може містити тільки літери")
    private String lastName;

    @NotBlank(message = "Логін не може бути порожнім")
    @Size(min = 6, max = 20, message = "Логін має містити від 6 до 20 символів")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Логін може містити тільки латинські літери та цифри")
    private String username;

    // ======== ГЕТТЕРИ І СЕТТЕРИ =========

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
